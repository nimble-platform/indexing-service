package eu.nimble.indexing.service.impl;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import eu.nimble.indexing.repository.AdditionalPropertyRepository;
import eu.nimble.indexing.repository.CatalogueItemRepository;
import eu.nimble.indexing.repository.model.catalogue.AdditionalProperty;
import eu.nimble.indexing.repository.model.catalogue.ICatalogueItem;
import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.service.CatalogueService;

@Service
public class CatalogueServiceImpl implements CatalogueService {
	private Logger logger = LoggerFactory.getLogger(CatalogueServiceImpl.class);

	private CatalogueItemRepository repo;
	private AdditionalPropertyRepository propRepo;
	

	@Autowired
	public void setCatalogueRepository(CatalogueItemRepository repository) {
		this.repo = repository;
	}
	@Autowired
	public void setAdditionalPropertyRepository(AdditionalPropertyRepository repository) {
		this.propRepo = repository;
	}

	@Resource
	private SolrTemplate solrTemplate;
	
	@Override
	public ItemType getItem(String uri) {
		return repo.findByUri(uri);
	}

	@Override
	public void setItem(ItemType item) {
		repo.save(item);
		
	}

	@Override
	public void removeItem(String uri) {
		ItemType item = getItem(uri);
		
		// delete the item when found
		if (item!=null) {
			// explicitly remove the nested documents
			Iterator<AdditionalProperty> p = item.getAdditionalProperty().iterator();
			while (p.hasNext()) {
				propRepo.delete(p.next());
			}
			// remove the item
			repo.delete(item);
		}		
	}
	
	@Override
	public FacetPage<ItemType> search(String query, Pageable pageable) {
		List<ItemType> fdx = repo.findByNameStartingWith(query, "en");
		Page<ItemType> p1 = repo.findItemByName(query, "en", pageable);
		Criteria q = new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD);
		if ( StringUtils.hasText(query) ) {
//			q = new Criteria(Criteria.WILDCARD).startsWith(query);
			
		}
		FacetQuery fq = new SimpleFacetQuery(q, pageable);
		
	
		fq.addFilterQuery(new SimpleFilterQuery(Criteria.where(ICatalogueItem.TYPE_FIELD).is("item")));
		fq.setFacetOptions(new FacetOptions(
				new SimpleField("label_en")
				, new SimpleField("label_es") 
//				, new FieldWithFacetParameters("cm_quantity")
//						.addFacetParameter(new FacetParameter("type", "terms"))
//						.addFacetParameter(new FacetParameter("field", "cm_quantity"))
//						.addFacetParameter(new FacetParameter("domain", new FacetParameter("blockParent", "doctype:item")))
				).setFacetMinCount(1));
		fq.addProjectionOnField(new SimpleField(CatalogueItemRepository.PARENT_FILTER));
		FacetPage<ItemType> result = solrTemplate.queryForFacetPage("item", fq, ItemType.class);
		result.getContent();
		for (Field field :  result.getFacetFields()) {
			Page<FacetFieldEntry> page = result.getFacetResultPage(field);

			for (FacetFieldEntry entry : page.getContent() ) {
				logger.debug("{} -> {} ({})", entry.getField().getName(), entry.getValue(), entry.getValueCount());
			}
		}
		return result;
	}

}
