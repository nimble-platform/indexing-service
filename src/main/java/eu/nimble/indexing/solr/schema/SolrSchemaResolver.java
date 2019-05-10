package eu.nimble.indexing.solr.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.solr.core.mapping.SolrPersistentEntity;
import org.springframework.data.solr.core.mapping.SolrPersistentProperty;
import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.data.solr.core.schema.SchemaDefinition.FieldDefinition;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author Christoph Strobl
 * @since 1.3
 */
public class SolrSchemaResolver {

	public SchemaDefinition resolveSchemaForEntity(SolrPersistentEntity<?> entity) {

		Assert.notNull(entity, "Schema cannot be resolved for 'null'.");

		final SchemaDefinition schemaDefinition = new SchemaDefinition(entity.getCollectionName());

		entity.doWithProperties((PropertyHandler<SolrPersistentProperty>) persistentProperty -> {

			FieldDefinition fieldDefinition = createFieldDefinitionForProperty(persistentProperty);
			if (fieldDefinition != null) {
				schemaDefinition.addFieldDefinition(fieldDefinition);
			}
		});
		List<FieldDefinition> copyOnly = new ArrayList<FieldDefinition>();
		schemaDefinition.getFields().forEach((Consumer<FieldDefinition>) def -> {
			if (! def.getCopyFields().isEmpty()) {
				for (String copyField : def.getCopyFields()) {
					if (! schemaDefinition.containsField(copyField)) {
						FieldDefinition copy = createFieldDefinitionForCopyField(def, copyField);
						if( copy !=null ) {
							copyOnly.add(copy);
						}
					}
				}
			}
		});
		for (FieldDefinition c : copyOnly) {
			if (! schemaDefinition.containsField(c.getName())) {
				schemaDefinition.addFieldDefinition(c);
			}
		}
		return schemaDefinition;
	}
	private boolean isMultiValued(SolrPersistentProperty property) {
		if ( property.isDynamicProperty()) {
			// when dynamic, the base type is a map
			Class<?> rawType = property.getMapValueType();
			return rawType.isArray() //
					|| Iterable.class.equals(rawType) //
					|| Collection.class.isAssignableFrom(rawType) //
					|| Streamable.class.equals(rawType);
		}
		else {
			return property.isMultiValued();
		}
	}
	@Nullable
	protected FieldDefinition createFieldDefinitionForProperty(@Nullable SolrPersistentProperty property) {

		if (property == null || property.isReadonly() || property.isTransient() || property.isChildProperty()) {
			return null;
		}

		FieldDefinition definition = new FieldDefinition(property.getFieldName());
		definition.setMultiValued(isMultiValued(property));
		definition.setIndexed(property.isSearchable());
		definition.setStored(property.isStored());
		definition.setType(property.getSolrTypeName());
		definition.setDefaultValue(property.getDefaultValue());
		definition.setRequired(property.isRequired());

		Collection<String> copyFields = property.getCopyFields();
		if (!CollectionUtils.isEmpty(copyFields)) {
			definition.setCopyFields(copyFields);
		}

		return definition;
	}
	@Nullable
	protected FieldDefinition createFieldDefinitionForCopyField(FieldDefinition source, String fieldName) {
		FieldDefinition definition = new FieldDefinition(fieldName);
		definition.setMultiValued(true);
		definition.setIndexed(source.isIndexed());
		definition.setType(source.getType());
		definition.setRequired(false);
		definition.setCopyFields(Collections.emptyList());
		return definition;
	}

}

