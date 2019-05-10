package eu.nimble.indexing.solr.schema;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationListener;
import org.springframework.data.mapping.context.MappingContextEvent;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.core.mapping.SolrPersistentEntity;
import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * @author Christoph Strobl
 * @since 1.3
 */
public class SolrPersistentEntitySchemaCreator implements ApplicationListener<MappingContextEvent<?, ?>> {

	public enum Feature {
		CREATE_MISSING_FIELDS
	}

	private SolrSchemaWriter schemaWriter;
	private SolrSchemaResolver schemaResolver;
	private ConcurrentHashMap<Class<?>, Class<?>> processed;

	private Set<Feature> features = new HashSet<>();

	public SolrPersistentEntitySchemaCreator(SolrClientFactory factory, SolrSchemaWriter schemaWriter) {
		super();
		this.schemaWriter = schemaWriter;
		this.schemaResolver = new SolrSchemaResolver();
		this.processed = new ConcurrentHashMap<>();
	}

	private void process(SolrPersistentEntity<?> entity) {

		SchemaDefinition schema = schemaResolver.resolveSchemaForEntity(entity);

		beforeSchemaWrite(entity, schema);
		schemaWriter.writeSchema(schema);
		afterSchemaWrite(entity, schema);
	}

	protected void beforeSchemaWrite(SolrPersistentEntity<?> entity, SchemaDefinition schema) {
		// before hook
	}

	protected void afterSchemaWrite(SolrPersistentEntity<?> entity, SchemaDefinition schema) {
		processed.put(entity.getType(), entity.getType());
	}

	@Override
	public void onApplicationEvent(MappingContextEvent<?, ?> event) {

		if (features.contains(Feature.CREATE_MISSING_FIELDS)) {
			
			if (event.getPersistentEntity() instanceof SolrPersistentEntity) {
				SolrPersistentEntity<?> entity = (SolrPersistentEntity<?>) event.getPersistentEntity();
				if (entity.isAnnotationPresent(SolrDocument.class)) {
					if (!processed.contains(entity.getType())) {
						process(entity);
					}
				}
			}
		}
	}

	public SolrPersistentEntitySchemaCreator enable(@Nullable Feature feature) {

		if (feature != null) {
			this.features.add(feature);
		}
		return this;
	}

	public SolrPersistentEntitySchemaCreator enable(Collection<Feature> features) {

		if (!CollectionUtils.isEmpty(features)) {
			this.features.addAll(features);
		}
		return this;
	}

	public SolrPersistentEntitySchemaCreator disable(Feature feature) {
		features.remove(feature);
		return this;
	}
}
