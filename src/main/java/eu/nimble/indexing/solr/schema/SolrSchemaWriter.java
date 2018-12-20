package eu.nimble.indexing.solr.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.data.solr.core.schema.SchemaDefinition.FieldDefinition;
import org.springframework.data.solr.core.schema.SchemaOperations;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.util.CollectionUtils;

public class SolrSchemaWriter extends org.springframework.data.solr.core.schema.SolrSchemaWriter {
	
	private final SolrCollectionOperations collectionOperations;
	public SolrSchemaWriter(SolrClientFactory solrClientFactory, SolrTemplate template) {
		// 
		super(solrClientFactory);
		this.collectionOperations = new SolrCollectionOperations(template);
	}

	protected void createSchema(SchemaDefinition schemaDefinition) {
		if ( collectionOperations.createCollection(schemaDefinition.getCollectionName()) ) {
			updateSchema(schemaDefinition);
		}
	}
	public void writeSchema(SchemaDefinition schemaDefinition) {
		if ( collectionOperations.collectionExists(schemaDefinition.getCollectionName())) {
			updateSchema(schemaDefinition);
		} else {
			// schema needs to be created
			createSchema(schemaDefinition);
			
		}

	}
	protected void updateSchema(SchemaDefinition schemaDefinition) {

		SchemaDefinition existing = collectionOperations.loadSchema(schemaDefinition.getCollectionName());

		List<FieldDefinition> fieldsToBeCreated = new ArrayList<>();
		for (FieldDefinition fieldDefinition : schemaDefinition.getFields()) {

			if (!existing.containsField(fieldDefinition.getName()))
				fieldsToBeCreated.add(fieldDefinition);
		}

		writeFieldDefinitions(fieldsToBeCreated, schemaDefinition.getCollectionName());
	}

	private void writeFieldDefinitions(Collection<FieldDefinition> definitions, String collectionName) {

		if (!CollectionUtils.isEmpty(definitions)) {

			SchemaOperations schemaOps = collectionOperations.getSchemaOperations(collectionName);
			for (FieldDefinition fd : definitions) {
				schemaOps.addField(fd);
			}
		}
	}
}
