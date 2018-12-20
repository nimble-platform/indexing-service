package eu.nimble.indexing.solr.schema;

import java.util.Map;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.data.solr.core.schema.SchemaDefinition.CopyFieldDefinition;
import org.springframework.data.solr.core.schema.SchemaDefinition.FieldDefinition;
import org.springframework.data.solr.core.schema.SchemaDefinition.SchemaField;
import org.springframework.data.solr.core.schema.SchemaModificationException;
import org.springframework.data.solr.core.schema.SchemaOperations;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class SolrSchemaOperations implements SchemaOperations {
	private final SolrTemplate template;
	private final String collection;

	public SolrSchemaOperations(String collection, SolrTemplate template) {

		Assert.hasText(collection, "Collection must not be null or empty!");
		Assert.notNull(template, "Template must not be null.");

		this.template = template;
		this.collection = collection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.solr.core.schema.SchemaOperations#getSchemaName()
	 */
	@Override
	public String getSchemaName() {

		return template
				.execute(solrClient -> new SchemaRequest.SchemaName().process(solrClient, collection).getSchemaName());

	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.solr.core.schema.SchemaOperations#getSchemaVersion()
	 */
	@Override
	public Double getSchemaVersion() {
		return template.execute(
				solrClient -> new Double(new SchemaRequest.SchemaVersion().process(solrClient, collection).getSchemaVersion()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.solr.core.schema.SchemaOperations#readSchema()
	 */
	@Override
	public SchemaDefinition readSchema() {

		SchemaRepresentation representation = template
				.execute(solrClient -> new SchemaRequest().process(solrClient, collection).getSchemaRepresentation());

		SchemaDefinition sd = new SchemaDefinition(collection);

		for (Map<String, Object> fieldValueMap : representation.getFields()) {
			sd.addFieldDefinition(FieldDefinition.fromMap(fieldValueMap));
		}
		for (Map<String, Object> fieldValueMap : representation.getDynamicFields()) {
			sd.addFieldDefinition(FieldDefinition.fromMap(fieldValueMap));
		}
		for (Map<String, Object> fieldValueMap : representation.getCopyFields()) {

			CopyFieldDefinition cf = CopyFieldDefinition.fromMap(fieldValueMap);
			sd.addCopyField(cf);

			if (sd.getFieldDefinition(cf.getSource()) != null) {
				sd.getFieldDefinition(cf.getSource()).setCopyFields(cf.getDestination());
			}
		}

		return sd;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.solr.core.schema.SchemaOperations#addField(org.springframework.data.solr.core.schema.SchemaDefinition.SchemaField)
	 */
	@Override
	public void addField(final SchemaField field) {

		if (field instanceof FieldDefinition) {
			addField((FieldDefinition) field);
		} else if (field instanceof CopyFieldDefinition) {
			addCopyField((CopyFieldDefinition) field);
		}
	}

	private void addField(final FieldDefinition field) {
		if ( field.getName().contains("*")) {
			// dynamic field
			template.execute(solrClient -> {
				
				UpdateResponse response = new SchemaRequest.AddDynamicField(field.asMap()).process(solrClient, collection);
				if (hasErrors(response)) {
					throw new SchemaModificationException(
							String.format("Adding field %s with args %s to collection %s failed with status %s. Server returned %s.",
									field.getName(), field.asMap(), collection, response.getStatus(), response));
				}
				return Integer.valueOf(response.getStatus());
			});
		}
		else {
			template.execute(solrClient -> {
				
				UpdateResponse response = new SchemaRequest.AddField(field.asMap()).process(solrClient, collection);
				if (hasErrors(response)) {
					throw new SchemaModificationException(
							String.format("Adding field %s with args %s to collection %s failed with status %s. Server returned %s.",
									field.getName(), field.asMap(), collection, response.getStatus(), response));
				}
				return Integer.valueOf(response.getStatus());
			});
			
		}

		if (!CollectionUtils.isEmpty(field.getCopyFields())) {

			CopyFieldDefinition cf = new CopyFieldDefinition();
			cf.setSource(field.getName());
			cf.setDestination(field.getCopyFields());

			addCopyField(cf);
		}
	}

	private void addCopyField(final CopyFieldDefinition field) {

		template.execute(solrClient -> {

			UpdateResponse response = new SchemaRequest.AddCopyField(field.getSource(), field.getDestination())
					.process(solrClient, collection);

			if (hasErrors(response)) {
				throw new SchemaModificationException(String.format(
						"Adding copy field %s with destinations %s to collection %s failed with status %s. Server returned %s.",
						field.getSource(), field.getDestination(), collection, response.getStatus(), response));
			}

			return Integer.valueOf(response.getStatus());
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.solr.core.schema.SchemaOperations#removeField(java.lang.String)
	 */
	@Override
	public void removeField(final String name) {

		template.execute(solrClient -> {

			try {
				UpdateResponse response = new SchemaRequest.DeleteField(name).process(solrClient, collection);
				if (hasErrors(response)) {
					throw new SchemaModificationException(
							String.format("Removing field with name %s from collection %s failed with status %s. Server returned %s.",
									name, collection, response.getStatus(), response));
				}

				return Integer.valueOf(response.getStatus());
			} catch (Exception e) {
				throw new SchemaModificationException(
						String.format("Removing field with name %s from collection %s failed.", name, collection));
			}
		});
	}

	private boolean hasErrors(UpdateResponse response) {

		if (response.getStatus() != 0
				|| response.getResponse() != null && !CollectionUtils.isEmpty(response.getResponse().getAll("errors"))) {
			return true;
		}

		return false;
	}
}
