package eu.nimble.indexing.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
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
		//
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
		return ItemType.JOIN_TO.getJoin(name);
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

	private void preProcessCustomProperties(ItemType t, Map<String, PropertyType> cp) {
		if ( cp != null && !cp.isEmpty()) {
			// 
			
			
			List<PropertyType> existing = propRepo.findByItemFieldNamesIn(cp.keySet());
			// keep a map of properties to change
			Map<String,PropertyType> changed = new HashMap<String,PropertyType>();
			
			// check whether an existing property lacks a itemFieldName
			existing.forEach(new Consumer<PropertyType>() {

				@Override
				public void accept(PropertyType c) {
					// 
					PropertyType change = cp.get(c.getLocalName());
					if ( change != null ) {
						// harmonize item field names
						for (String idxField : change.getItemFieldNames()) {
							if (! c.getItemFieldNames().contains(idxField)) {
								c.addItemFieldName(idxField);
								// add to changed to have it saved ...
								changed.put(t.getLocalName(), c);
							}
						}
						// harmonize labels
						harmonizeLabels(c.getLabel(), change.getLabel());
						harmonizeLabels(c.getComment(), change.getComment());
						harmonizeLabels(c.getDescription(), change.getDescription());
						// remove any existing property so that is not added twice  
						cp.remove(c.getLocalName());
					}
				}
			});
			// process the remainder
			cp.forEach(new BiConsumer<String, PropertyType>() {

				@Override
				public void accept(String qualifier, PropertyType newProp) {
					PropertyType pt = new PropertyType();
					// how to specify uri, localName & nameSpace
					// TODO - use namespace from config
					pt.setUri("urn:nimble:custom:"+ qualifier);
					pt.setNameSpace("urn:nimble:custom:");
					pt.setLocalName(qualifier);
					pt.setItemFieldNames(newProp.getItemFieldNames());
					pt.setLabel(newProp.getLabel());
					pt.setComment(newProp.getComment());
					pt.setDescription(newProp.getDescription());
					pt.setPropertyType("CustomProperty");
					pt.setValueQualifier(newProp.getValueQualifier());
					switch (pt.getValueQualifier()) {
					case "BOOLEAN":
						pt.setRange(XSD.xboolean.getURI());
						break;
					case "TEXT":
						pt.setRange(XSD.xstring.getURI());
						break;
					default:
						pt.setRange(XSD.xdouble.getURI());
						break;
					}
					// 
					changed.put(qualifier, pt);
				}});

			//
			for ( PropertyType newPt : changed.values()) {
				propRepo.save(newPt);
			}
		}
	}
	private void harmonizeLabels(Map<String,String> toAdd, Map<String, String> from) {
		if ( toAdd != null && from != null) {
			for ( String lang : from.keySet()) {
				toAdd.putIfAbsent(lang,  from.get(lang));
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
