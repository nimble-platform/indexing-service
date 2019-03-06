package eu.nimble.indexing.solr.schema;

import java.util.Collections;

import org.springframework.data.solr.core.RequestMethod;
import org.springframework.data.solr.core.convert.SolrConverter;
import org.springframework.data.solr.core.schema.SchemaOperations;
import org.springframework.data.solr.server.SolrClientFactory;

import eu.nimble.indexing.solr.schema.SolrPersistentEntitySchemaCreator.Feature;


public class SolrTemplate extends org.springframework.data.solr.core.SolrTemplate {

	public SolrTemplate(SolrClientFactory solrClientFactory) {
		super(solrClientFactory);
		
		setSchemaCreationFeatures(Collections.singletonList(org.springframework.data.solr.core.schema.SolrPersistentEntitySchemaCreator.Feature.CREATE_MISSING_FIELDS));
		setMappingContext(new SimpleMappingContext(
				new SolrPersistentEntitySchemaCreator(solrClientFactory, new SolrSchemaWriter(solrClientFactory, this))
						.enable(Feature.CREATE_MISSING_FIELDS)));
	}

	public SolrTemplate(SolrClientFactory solrClientFactory, SolrConverter solrConverter,
			RequestMethod defaultRequestMethod) {
		super(solrClientFactory, solrConverter, defaultRequestMethod);
		setSchemaCreationFeatures(Collections.singletonList(org.springframework.data.solr.core.schema.SolrPersistentEntitySchemaCreator.Feature.CREATE_MISSING_FIELDS));
	}

	/**
	 * Provide a {@link SchemaOperations} which can handle dynamic fields
	 */
	@Override
	public SchemaOperations getSchemaOperations(String collection) {

		return new SolrSchemaOperations(collection, this);
	}

	@Override
	public RequestMethod getDefaultRequestMethod() {
		return RequestMethod.POST;
	}


}
