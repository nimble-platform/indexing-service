package eu.nimble.indexing.repository;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrTemplate;

public class CustomizedItemRepositoryImpl implements CustomizedItemRepository {
	private Logger logger = LoggerFactory.getLogger(CustomizedItemRepositoryImpl.class);

	@Resource
	private SolrTemplate solrTemplate;


}
