package eu.nimble.indexing.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.Join;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.ClassRepository;
import eu.nimble.indexing.repository.ItemRepository;
import eu.nimble.indexing.repository.PartyRepository;
import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.service.ItemService;
import eu.nimble.service.model.solr.IndexField;
import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.ClassType;
import eu.nimble.service.model.solr.owl.Concept;
import eu.nimble.service.model.solr.owl.PropertyType;
import eu.nimble.service.model.solr.party.PartyType;

@Service
public class ItemServiceImpl extends SolrServiceImpl<ItemType> implements ItemService {

	@Autowired 
	ItemRepository itemRepo;
	
	@Autowired 
	PartyRepository partyRepo;
	
	@Autowired
	PropertyRepository propRepo;
	
	@Autowired
	ClassRepository classRepo;

	@Override
	public String getCollection() {
		return ItemType.COLLECTION;
	}

	@Override
	public Class<ItemType> getSolrClass() {
		return ItemType.class;
	}

	@Override
	public long setCatalogue(String catalogueId, List<ItemType> items) {
		long i = 0;
		for ( ItemType item : items ) {
			if (item.getCatalogueId() == null || item.getCatalogueId().equals(catalogueId)) {
				set(item);
				i++;
			}
		}
		return i;
	}

	@Override
	public long deleteCatalogue(String catalogueId) {
		return itemRepo.deleteByCatalogueId(catalogueId);
	}

	@Override
	protected void prePersist(ItemType t) {
		preProcessPartyType(t, t.getManufacturer());
		// check for non-existing properties
		preProcessCustomProperties(t, t.getCustomProperties());
	}
	
	@Override
	protected void postSelect(ItemType t) {
		postProcessManufacturer(t);
		postProcessClassification(t);;
	}

	@Override
	protected void enrichContent(List<ItemType> content) {
		enrichManufacturers(content);
		
	}

	@Override
	protected void enrichFields(Map<String, IndexField> inUse) {
		Set<String> itemFieldNames = new HashSet<>();
		for (IndexField s : inUse.values()) {
			itemFieldNames.add(s.getMappedName());
		}
		if ( itemFieldNames.size()>0 ) {
			// obtain a map of matching properties
			final Map<Collection<String>,PropertyType> properties = propRepo.findByItemFieldNamesIn(itemFieldNames)
					.stream()
					.collect(Collectors.toMap(PropertyType::getItemFieldNames, c -> c));
			
			// 
			for ( IndexField s : inUse.values()) {
				for (Collection<String> keys : properties.keySet()) {
					if ( keys.contains(s.getMappedName() )) {
						PropertyType p = properties.get(keys);
						s.withNamed(p);
					}
				}
			}
		}
	}


	@Override
	protected Join getJoin(String name) {
		try {
			// check for ItemType JOINS
			ItemType.JOIN_TO join = ItemType.JOIN_TO.valueOf(name);
			// 
			return join.getJoin();
		} catch (Exception e) {
			// invalid join
			return null;
		}
	}
	private void postProcessManufacturer(ItemType t) {
		if ( t.getManufacturerId() != null ) {
			Optional<PartyType> p = partyRepo.findById(t.getManufacturerId());
			if ( p.isPresent()) {
				t.setManufacturer(p.get());
			}
		}
	}
	private void postProcessClassification(ItemType t) {
		if ( t.getClassificationUri() != null && !t.getClassificationUri().isEmpty()) {
			List<ClassType> c = classRepo.findByUriIn(asSet(t.getClassificationUri()));
			c.forEach(new Consumer<ClassType>() {

				@Override
				public void accept(ClassType ic) {
					t.addClassification(Concept.buildFrom(ic));
				}
			});
		}
	}
	private void preProcessPartyType(ItemType t, PartyType m) {
		if ( m != null) {
			Optional<PartyType> pt = partyRepo.findById(m.getId());
			if (! pt.isPresent() ) {
				// be sure to have the manufacturer in the index
				partyRepo.save(m);
			}
			// ensure the manufacturer id is in the indexed field 
			t.setManufacturerId(m.getId());
		}
	}
	
	private void preProcessCustomProperties(ItemType t, Map<String, Concept> cp) {
		if ( cp != null && !cp.isEmpty()) {
			List<PropertyType> existing = propRepo.findByItemFieldNamesIn(cp.keySet());
			//
			// remove existing properties
			for ( PropertyType pt : existing) {
				for ( String name : pt.getItemFieldNames()) {
					if (cp.containsKey(name) ) {
						cp.remove(name);
					}
				}
			}
			// cp contains new concepts ...
			for ( String key : cp.keySet()) {
				Concept c = cp.get(key);
				PropertyType pt = new PropertyType();
				// how to specify uri, localName & nameSpace
				// TODO - use namespace from config
				pt.setUri("urn:nimble:custom:"+ key);
				pt.setNameSpace("urn:nimble:custom:");
				pt.setLocalName(key);
				pt.setItemFieldNames(Collections.singleton(key));
				pt.setLabel(c.getLabel());
				pt.setComment(c.getComment());
				pt.setDescription(c.getDescription());
				pt.setPropertyType("CustomProperty");
				// 
				if ( t.getBooleanValue().containsKey(key)) {
					pt.setRange(XSD.xboolean.getURI());
					pt.setValueQualifier("BOOLEAN");
				}
				if ( t.getStringValue().containsKey(key)) {
					pt.setRange(XSD.xstring.getURI());
					pt.setValueQualifier("TEXT");
				}
				if ( t.getDoubleValue().containsKey(key)) {
					pt.setRange(XSD.xdouble.getURI());
					pt.setValueQualifier("REAL_MEASURE");
				}
				
				propRepo.save(pt);
			}
		}
		
	}
	private Set<String> extractManufacturers(List<ItemType> items) {
		return items.stream()
				.map(ItemType::getManufacturerId)
				.collect(Collectors.toSet());
	}
	private void enrichManufacturers(List<ItemType> items) {
		// read all existing manufacturers
		if ( items != null && !items.isEmpty()) {
			final Map<String, PartyType> map = partyRepo.findByUriIn(extractManufacturers(items)).stream()
					.collect(Collectors.toMap(PartyType::getUri, p->p));
			
			items.forEach(new Consumer<ItemType>() {
				
				@Override
				public void accept(ItemType t) {
					if ( map.containsKey(t.getManufacturerId())) {
						// add the retrieved customer to the actual list
						t.setManufacturer(map.get(t.getManufacturerId()));
					}
				}
			});
		}
	}



}
