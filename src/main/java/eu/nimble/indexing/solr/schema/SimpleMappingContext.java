package eu.nimble.indexing.solr.schema;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.solr.core.mapping.SimpleSolrPersistentEntity;
import org.springframework.data.solr.core.mapping.SimpleSolrPersistentProperty;
import org.springframework.data.solr.core.mapping.SolrPersistentProperty;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;

/**
 * Solr specific implementation of {@link org.springframework.data.mapping.context.MappingContext}
 * 
 * @author Christoph Strobl
 */
public class SimpleMappingContext
		extends AbstractMappingContext<SimpleSolrPersistentEntity<?>, SolrPersistentProperty> {

	public SimpleMappingContext() {
		this(null);
	}

	public SimpleMappingContext(@Nullable SolrPersistentEntitySchemaCreator schemaCreator) {
		if (schemaCreator != null) {
			setApplicationEventPublisher(new SolrMappingEventPublisher(schemaCreator));
		}
	}

	@Override
	protected <T> SimpleSolrPersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
		return new SimpleSolrPersistentEntity<>(typeInformation);
	}

	@Override
	protected SolrPersistentProperty createPersistentProperty(Property property, SimpleSolrPersistentEntity<?> owner,
			SimpleTypeHolder simpleTypeHolder) {
		return new SimpleSolrPersistentProperty(property, owner, simpleTypeHolder);
	}

}