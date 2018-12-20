package eu.nimble.indexing.repository.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.mapping.SolrPersistentEntity;
import org.springframework.data.solr.core.mapping.SolrPersistentProperty;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.CatalogueItemBaseRepository;
import eu.nimble.indexing.repository.CatalogueItemRepository;
import eu.nimble.indexing.repository.model.catalogue.ICatalogueItem;
import eu.nimble.indexing.repository.model.catalogue.ItemType;

@Repository
public class CatalougeItemRepositoryImpl implements CatalogueItemBaseRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogueItemRepository.class);
	
	@Resource
	private SolrTemplate solrTemplate;

	@Override
	public Page<ItemType> findItemByName(String name, String language, Pageable pageable) {
		// TODO Auto-generated method stub
		Query query = new SimpleQuery(name);
		SolrPersistentEntity<?> theType = solrTemplate.getConverter().getMappingContext().getPersistentEntity(ItemType.class);
		SolrPersistentProperty property = theType.getPersistentProperty("additionalProperty");
		if ( property.isChildProperty() ) {
			SolrPersistentEntity<?> child = solrTemplate.getConverter().getMappingContext().getPersistentEntity(property.getActualType());
			
		}
		query.addFilterQuery(new SimpleQuery(new Criteria(ICatalogueItem.TYPE_FIELD).is(ItemType.TYPE_VALUE)));
		query.setPageRequest(pageable);
		return solrTemplate.queryForPage("item", query, ItemType.class);
	}


	
	
}
