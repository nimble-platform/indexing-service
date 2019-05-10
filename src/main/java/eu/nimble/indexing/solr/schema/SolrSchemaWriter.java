package eu.nimble.indexing.solr.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
		SchemaOperations schemaOps = collectionOperations.getSchemaOperations(schemaDefinition.getCollectionName());

		
		for (FieldDefinition fieldDefinition : schemaDefinition.getFields()) {
			if (! fieldDefinition.getCopyFields().isEmpty()) {
				writeCopyToTarget(schemaOps, fieldDefinition.getCopyFields(), schemaDefinition, existing);
			}
			if (!existing.containsField(fieldDefinition.getName())) {
				// add the missing field, any copy target must be already present
				schemaOps.addField(fieldDefinition);
			}
		}
	}
	private void writeCopyToTarget(SchemaOperations schemaOps, List<String> copyTo, SchemaDefinition definition, SchemaDefinition existing) {
		for (String copyField : copyTo ) {
			if ( !existing.containsField(copyField)) {
				FieldDefinition target = definition.getFieldDefinition(copyField);
				if ( target!= null) {
					if (! target.getCopyFields().isEmpty()) {
						writeCopyToTarget(schemaOps, target.getCopyFields(), definition, existing);
					}
					// add the copy target to the schema & to existing
					schemaOps.addField(target);
					existing.addFieldDefinition(target);
				}
			}
		}
	}
}
