package eu.nimble.indexing.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.AnyCriteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.model.Concept;
import eu.nimble.indexing.model.IndexField;
import eu.nimble.indexing.model.SearchResult;
import eu.nimble.indexing.repository.ClassRepository;
import eu.nimble.indexing.repository.ItemRepository;
import eu.nimble.indexing.repository.PartyTypeRepository;
import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.repository.model.ItemUtils;
import eu.nimble.indexing.repository.model.catalogue.IParty;
import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.repository.model.catalogue.PartyType;
import eu.nimble.indexing.repository.model.owl.ClassType;
import eu.nimble.indexing.repository.model.owl.PropertyType;
import eu.nimble.indexing.service.CatalogueService;
import eu.nimble.indexing.solr.query.ParentFilterField;

@Service
public class CatalogueServiceImpl implements CatalogueService {
	private Logger logger = LoggerFactory.getLogger(CatalogueServiceImpl.class);

	private ItemRepository itemRepo;
	
	private PropertyRepository propertyRepo;
	private ClassRepository classRepo;

	private PartyTypeRepository partyRepo;

	@Autowired
	public void setItemRepository(ItemRepository itemRepo) {
		this.itemRepo = itemRepo;	
	}
	@Autowired
	public void setPropertyRepository(PropertyRepository repo) {
		this.propertyRepo = repo;
	}
	@Autowired
	public void setClassRepository(ClassRepository repo) {
		this.classRepo = repo;
	}
	@Autowired
	public void setPartyRepository(PartyTypeRepository repo) {
		this.partyRepo = repo;
	}

	@Resource
	private SolrTemplate solrTemplate;
	
	@Override
	public ItemType getItem(String uri) {
		Optional<ItemType> item = itemRepo.findById(uri);
		if ( item.isPresent()) {
			ItemType i = item.get();
			return enrichFromSolr(i);
		}
		return null;
	}

	@Override
	public void setItem(ItemType item) {
		// check the manufacturers provided inline
		processManufacturerFromItem(item);
		itemRepo.save(item);
	}
	/**
	 * Helper method checking an arbitrary provided
	 * manufacturer party with the index. if not present
	 * the manufacturer is added to the index
	 * @param item
	 */
	private void processManufacturerFromItem(ItemType item) {
		if ( item.getManufacturer()!=null) {
			Optional<PartyType> pt = partyRepo.findById(item.getManufacturer().getId());
			if (! pt.isPresent() ) {
				// be sure to have the manufacturer in the index
				partyRepo.save(item.getManufacturer());
			}
			// ensure the manufacturer id is in the indexed field 
			item.setManufacturerId(item.getManufacturer().getId());
		}
		
	}
	private ItemType enrichFromSolr(ItemType item) {
		if ( item.getManufacturerId()!=null) {
			Optional<PartyType> p = partyRepo.findById(item.getManufacturerId());
			if ( p.isPresent()) {
				item.setManufacturer(p.get());
				item.setManufacturerId(null);
			}
		}
		if ( item.getClassificationUri() != null ) {
			Set<String> s = new HashSet<>();
			s.addAll(item.getClassificationUri());
			List<ClassType> cTypeList = classRepo.findByUriIn(s);
			for (ClassType c : cTypeList) {
				item.addClassification(Concept.buildFrom(c));
			}
		}
		return item;
	}
	/**
	 * Helper method to enrich an result set with the corresponding
	 * manufacturers.
	 * 
	 * @param items
	 */
	private void processManufacturersFromList(Iterable<ItemType> items) {
		final List<String> manufacturers = new ArrayList<>(); 
							
		items.forEach(new Consumer<ItemType>() {

			@Override
			public void accept(ItemType t) {
				String id = t.getManufacturerId();
				if ( id!=null && ! manufacturers.contains(id))
					manufacturers.add(id);
				
			}
			
		});
		final Map<String,PartyType> mList = partyRepo.findByIdIn(manufacturers)
				.stream()
				.collect(Collectors.toMap(PartyType::getId, c -> c));
		
		items.forEach(new Consumer<ItemType>() {

			@Override
			public void accept(ItemType t) {
				t.setManufacturer(mList.get(t.getManufacturerId()));
				
			}
		});
	}
	/**
	 * Helper method to enrich a result set with he commodity classification 
	 * @param items
	 */
	private void processCommodityFromList(Iterable<ItemType> items) {
		if ( items != null ) {
			final Set<String> uriSet = new HashSet<String>();
			
			items.forEach(new Consumer<ItemType>() {
				
				@Override
				public void accept(ItemType t) {
					if ( t.getClassificationUri()!=null ) {
						for (String s : t.getClassificationUri()) {
							uriSet.add(s);
						}
					}
				}
			});
			if (! uriSet.isEmpty()) {
				final Map<String, ClassType> commodity = classRepo.findByUriIn(uriSet)
						.stream()
						.collect(Collectors.toMap(ClassType::getUri, c -> c));
				
				for (final ItemType i : items) {
					i.getClassificationUri().stream().forEach(new Consumer<String>() {
						
						@Override
						public void accept(String t) {
							ClassType c = commodity.get(t);
							Concept concept = Concept.buildFrom(c);
							i.addClassification(concept);
						}});
				}
			}
		}
	}
	@Override
	public void removeItem(String uri) {
		ItemType item = getItem(uri);
		
		// delete the item when found
		if (item!=null) {
			// explicitly remove the nested documents
			
//			Iterator<AdditionalProperty> p = item.getAdditionalProperty().iterator();
//			while (p.hasNext()) {
//				propRepo.delete(p.next());
//			}
			// remove the item
			itemRepo.delete(item);
		}		
	}
	 /*
	  * path=/select params={q=canCompany
	  * &facet.field=materialProperty_s
	  * &facet.field=linkToConfigurator_s
	  * &facet.field=palletDimensionGrossWeightKilos_q
	  * &facet.field=palletDimensionGrossWeight_unit
	  * &facet.field=weightOfTheIndividualPackagingKilos_q
	  * &facet.field=weightGlm_q
	  * &facet.field=trust_company_trading_volume&facet.field=prop_s&facet.field=color_s&facet.field=bxHxTCm_q&facet.field=productShape_s
	  * &facet.field=hasAdjustableHeight_b
	  * &facet.field=item_price_currency&facet.field=height_d&facet.field=colorCode_s&facet.field=manufacturerProductDescription_s
	  * &facet.field=materialOfTheArmrestPadding_s&facet.field=adjustableArmrest_b&facet.field=bxHxT_value&facet.field=id&facet.field=season_s
	  * &facet.field=nominalThickness_unit&facet.field=hasWeightInKilograms_d&facet.field=minHeightCm_q&facet.field=fabricCategory_s
	  * &facet.field=manufacturerName_s&facet.field=supplierProductDesignation_s&facet.field=widthCm_q
	  * &facet.field=weightOfTheContainedSubstanceSVHC_value&facet.field=item_commodity_classification_mix&facet.field=estimated_duration
	  * &facet.field=item_commodity_classification&facet.field=robotjointspeed_unit&facet.field=bxHxT_unit&facet.field=prop1_s
	  * &facet.field=palletDimensionGrossWeight_value&facet.field=weight_unit&facet.field=lmf.created&facet.field=supplierProductOrderSuffix_s
	  * &facet.field=supported_cargo_type&facet.field=item_image&facet.field=width_unit&facet.field=width_value&facet.field=trust_company_trust_score
	  * &facet.field=item_commodity_classification_uri&facet.field=hasWidth_d&facet.field=typeOfPackaging_s&facet.field=item_package_amount_unit
	  * &facet.field=netWeightKilos_q&facet.field=quantityOfProductsPerPalletLayer_d&facet.field=length_value
	  * &facet.field=manufacturer_ppap_compliance_level
	  * &facet.field=p1_s&facet.field=lmf.indexed&facet.field=compliesWithLegislation_s
	  * &facet.field=trust_company_rating
	  * &facet.field=item_free_of_charge_indicator&facet.field=lmf.type&facet.field=technique_s&facet.field=retailerName_s&facet.field=productType_s
	  * &facet.field=adjustmentRangeOfTheArmrestHeightDegrees_q&facet.field=customsTariffNumberTARIC_d&facet.field=nominalThickness_value
	  * &facet.field=numberOfTilesPerSquareMeter_d&facet.field=finalUse_s&facet.field=trust_company_seller_communication
	  * &facet.field=item_price&facet.field=productCheckDateSVHC_s&facet.field=robotjointspeedMs_q&facet.field=item_name&facet.field=floorTiles_s
	  * &facet.field=robotjointspeed_value&facet.field=maxClearWidthOfArmrest_value&facet.field=heightCm_q&facet.field=nominalThicknessMm_q
	  * &facet.field=sleve_s&facet.field=colorOfArmrest_s&facet.field=item_catalogue_id&facet.field=supported_product_nature
	  * &facet.field=groundTiles_s&facet.field=lmf.spellcheck&facet.field=robotjointcolor_s&facet.field=item_description&facet.field=composition_s
	  * &facet.field=madeInCountry_s&facet.field=materialOfTheArmrest_s&facet.field=productArticleNumberOfManufacturer_s&facet.field=minHeight_unit
	  * &facet.field=collectionName_s&facet.field=item_manufacturer_name&facet.field=brand_s&facet.field=trust_company_fulfillment_of_terms
	  * &facet.field=length_unit&facet.field=mode&facet.field=maxSeatHeightCm_q&facet.field=widthMm_q&facet.field=maxHeight_value
	  * &facet.field=quantityOfLayersPerPallet_d&facet.field=item_commodity_classification_code&facet.field=estimated_duration_unit
	  * &facet.field=item_manufacturer_id&facet.field=total_capacity&facet.field=antistaticPresent_b&facet.field=lengthM_q&facet.field=item_properties
	  * &facet.field=hasColour_s&facet.field=gTIN_s&facet.field=emission_standard&facet.field=maxSeatHeight_value
	  * &facet.field=toleranceClass_s&facet.field=hasHeight_d&facet.field=weightOfTheIndividualPackaging_value&facet.field=trust_company_delivery_packaging
	  * &facet.field=line_s&facet.field=total_capacity_unit&facet.field=qualityCharacteristicRecordOfIngoingMaterial_s
	  * &facet.field=adjustmentRangeOfTheArmrestHeight_value&facet.field=retailerIdentification_s&facet.field=materialOfTheSeatBack_s
	  * &facet.field=supplierProductDescription_s&facet.field=manufacturerProductDesignation_s&facet.field=hasLength_d&facet.field=grade_s
	  * &facet.field=item_package_type&facet.field=containsSubstanceAccordingToSVHC_b&facet.field=packagingDimensions_s&facet.field=heightM_q
	  * &facet.field=item_certificate&facet.field=designation_s&facet.field=item_package_amount&facet.field=width_d
	  * &facet.field=item_estimated_delivery_time_unit&facet.field=basin_s&facet.field=prop2_d&facet.field=conditionOfSurface_s
	  * &facet.field=netWeight_unit&facet.field=maxHeightCm_q&facet.field=item_applicable_countries&facet.field=hasSeatBelt_b
	  * &facet.field=item_manufacturer_origin&facet.field=minHeight_value&facet.field=material_s&facet.field=lmf.uri&facet.field=hasName_s
	  * &facet.field=netWeight_value&facet.field=cardboardWeightPortionOfTheIndividualPackaging_value&facet.field=renderMaterial_s
	  * &facet.field=materialOfTheFrame_s&facet.field=neck_s&facet.field=height_unit&facet.field=maxClearWidthOfArmrestCm_q
	  * &facet.field=item_id&facet.field=weight_value&facet.field=weight_s&facet.field=supplierProductFamily_s
	  * &facet.field=toilet_s&facet.field=adjustmentRangeOfTheArmrestHeight_unit&facet.field=manufacturer_certificate
	  * &facet.field=service_type&facet.field=application_s&facet.field=colorOfSittingAreaOfSeat_s&facet.field=productArticleNumberOfSupplier_s
	  * &facet.field=_version_&facet.field=item_estimated_delivery_time&facet.field=testCustomProperty_d
	  * &facet.field=trust_company_number_of_transactions&facet.field=maxClearWidthOfArmrest_unit&facet.field=compliesWithRegulation_s
	  * &facet.field=weightOfTheIndividualPackaging_unit&facet.field=applicationConditionsForCompositionOfPlates_s&facet.field=height_value
	  * &facet.field=hasDescriptionOfMaterials_s&facet.field=hasDescription_s&json.nl=map&start=0&sort=score+desc&facet.mincount=1&rows=10
	  * &facet=true&wt=json&facet.sort=count} hits=24 status=0 QTime=52 
	  */
	public SearchResult<ItemType> search(String query, Set<String> filterQuery, Set<String> facetField) {
		return null;
	}
	@Override
	public SearchResult<ItemType> search(String query, Pageable pageable) {
		
		FacetQuery fq = new SimpleFacetQuery(AnyCriteria.any(), pageable);
		// add filter queries 
		
		fq.addFilterQuery(ItemUtils.doctypeFilter());
//		fq.addFilterQuery(ItemUtils.nestedFieldFilter("valueQualifier", "quantity"));
		fq.addFilterQuery(ItemUtils.filterManufacturerField(IParty.NAME_FIELD, "nimble*"));
		//set field list
		fq.addProjectionOnField(new SimpleField("*"));
		// add faceting options
		// 
		fq.setFacetOptions(new FacetOptions(
				new SimpleField("languages"),
				new SimpleField("en_label")
				).setFacetMinCount(0));	
		// run the query
		FacetPage<ItemType> result = solrTemplate.queryForFacetPage("item",fq, ItemType.class);
		// 
		processManufacturersFromList(result.getContent());
		//
		processCommodityFromList(result.getContent());
		SearchResult<ItemType> res = new SearchResult<>(result.getContent());
		res.setTotalElements(result.getTotalElements());
		res.setTotalPages(result.getTotalPages());
		res.setCurrentPage(result.getNumber());
		res.setPageSize(result.getSize());
		
		
		for (Field field :  result.getFacetFields()) {
			Page<FacetFieldEntry> page = result.getFacetResultPage(field);
			//
			for (FacetFieldEntry entry : page.getContent() ) {
				res.addFacet(entry.getField().getName(), entry.getValue(), entry.getValueCount());
				logger.debug("{} -> {} ({})", entry.getField().getName(), entry.getValue(), entry.getValueCount());
			}
		}
		return res;
	}
	public Collection<IndexField> fieldsInUse() {
		LukeRequest luke = new LukeRequest();
		luke.setShowSchema(false);
		try {
			LukeResponse resp = luke.process(solrTemplate.getSolrClient(), "item");
			
			@SuppressWarnings("unchecked")
			NamedList<Object> fields = (NamedList<Object>) resp.getResponse().get("fields");
			Map<String,IndexField> inUse = getFields(fields);
			enrichFields(inUse);
			return inUse.values();
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	private void enrichFields(final Map<String, IndexField> inUse) {
		Set<String> itemFieldNames = new HashSet<>();
		for (IndexField s : inUse.values()) {
			itemFieldNames.add(s.getFieldName());
			if (s.getDynamicBase()!=null )
				itemFieldNames.add(s.getDynamicBase());
//			String part = s.getDynamicNamePart();
//			if ( part !=null) {
//				itemFieldNames.add(part);
//			}
		}
		final Map<Collection<String>,PropertyType> properties = propertyRepo.findByItemFieldNamesIn(itemFieldNames)
				.stream()
				.collect(Collectors.toMap(PropertyType::getItemFieldNames, c -> c));
		
		// enrich IndexField
		for ( IndexField s : inUse.values()) {
			for (Collection<String> keys : properties.keySet()) {
				if ( keys.contains(s.getFieldName()) || keys.contains(s.getDynamicBase()) ) {
					PropertyType p = properties.get(keys);
					s.withNamed(p);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	private Map<String, IndexField> getFields(NamedList<Object> fields)  {
		Map<String, IndexField> ffield = new HashMap<>();
		for (Map.Entry<String, Object> field : fields) {
			String name = field.getKey();
			IndexField f = new IndexField(name);
			for (Entry<String, Object> prop : (NamedList<Object>)field.getValue()) {
				switch(prop.getKey()) {
				case "type":
					f.setDataType(prop.getValue().toString());
					break;
				case "docs":
					f.setDocCount(Integer.valueOf(prop.getValue().toString()));
					break;
				case "dynamicBase":
					f.setDynamicBase(prop.getValue().toString());
					break;
				}
			}
			ffield.put(name, f);
		}
		
		return ffield;
	}
	@Override
	public boolean setItems(List<ItemType> items) {
//		Map<String, PartyType> manufacturer = new HashMap<>();
		// @TODO : extract all manufacturers from the items and do a bunch checking
		for ( ItemType i : items) {
			setItem(i);
		}
		return true;
	}
	
}
