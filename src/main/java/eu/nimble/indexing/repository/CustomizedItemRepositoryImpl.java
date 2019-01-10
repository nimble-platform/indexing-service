package eu.nimble.indexing.repository;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.AnyCriteria;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;

import eu.nimble.indexing.repository.model.ItemUtils;
import eu.nimble.indexing.repository.model.catalogue.IParty;
import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.solr.query.ParentFilterField;

public class CustomizedItemRepositoryImpl implements CustomizedItemRepository {
	private Logger logger = LoggerFactory.getLogger(CustomizedItemRepositoryImpl.class);

	@Resource
	private SolrTemplate solrTemplate;

	@Override
	public FacetPage<ItemType> findItemByName(String name, String language, Pageable pageable) {
		// TODO Auto-generated method stub
		
		FacetQuery fq = new SimpleFacetQuery(AnyCriteria.any(), pageable);
		// add filter queries 
		
		fq.addFilterQuery(ItemUtils.doctypeFilter());
		fq.addFilterQuery(ItemUtils.nestedFieldFilter("valueQualifier", "quantity"));
		fq.addFilterQuery(ItemUtils.filterManufacturerField(IParty.NAME_FIELD, "Nimble*"));
		//set field list
		fq.addProjectionOnField(new SimpleField("*"));
		fq.addProjectionOnField(new ParentFilterField(ItemUtils.doctypeFilter()));
		// add faceting options
		// 
		// run the query
		FacetPage<ItemType> result = solrTemplate.query("item",fq, ItemType.class);
		for (Field field :  result.getFacetFields()) {
			Page<FacetFieldEntry> page = result.getFacetResultPage(field);

			for (FacetFieldEntry entry : page.getContent() ) {
				logger.debug("{} -> {} ({})", entry.getField().getName(), entry.getValue(), entry.getValueCount());
			}
		}
		return result;

	}

}
