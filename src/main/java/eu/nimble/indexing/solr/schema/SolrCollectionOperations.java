package eu.nimble.indexing.solr.schema;

import java.util.List;

import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.data.solr.core.schema.SchemaOperations;

public class SolrCollectionOperations {
	private final SolrTemplate solrTemplate;

	public SolrCollectionOperations(SolrTemplate template) {
		this.solrTemplate = template;
	}

	public SchemaOperations getSchemaOperations(String collectionName) {
		return solrTemplate.getSchemaOperations(collectionName);
	}

	public SchemaDefinition loadSchema(String collectionName) {
		return this.solrTemplate.getSchemaOperations(collectionName).readSchema();
	}

	@SuppressWarnings("unchecked")
	public boolean collectionExists(String collectionName) {
		return solrTemplate.execute(solrClient -> ((List<String>)new CollectionAdminRequest.List().process(solrClient)
				// result is a CollectionAdminResponse
				.getResponse().get("collections")).contains(collectionName));
	}

	public boolean createCollection(String collectionName) {
		
		return solrTemplate.execute(solrClient -> CollectionAdminRequest.createCollection(collectionName, 1, 1)
				.process(solrClient).isSuccess());

	}

}
