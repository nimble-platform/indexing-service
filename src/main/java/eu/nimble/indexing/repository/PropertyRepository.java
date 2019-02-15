package eu.nimble.indexing.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.service.model.solr.owl.IPropertyType;
import eu.nimble.service.model.solr.owl.PropertyType;

@Repository
public interface PropertyRepository  extends SolrCrudRepository<PropertyType, String>{
	/**
	 * Obtain a single property by it's uri
	 * @param uri
	 * @return
	 */
	List<PropertyType> findByUri(String uri);
	/**
	 * Retrieve all properties for a distinct product
	 * @param product The product's uri
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD
		})
	List<PropertyType> findByProduct(String product);
	/**
	 * Find all properties assigned to one of the provided products
	 * @param products
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD
		})
	List<PropertyType> findByProductIn(Set<String> products);
	/**
	 * 
	 * @param namespace
	 * @param localNames
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD
		})
	List<PropertyType> findByNameSpaceAndLocalNameIn(String namespace, Set<String> localNames);
	/**
	 * Retrieve multiple properties by their uri
	 * @param uri list of URI's to resolve
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD
		})
	List<PropertyType> findByUriIn(Set<String> uri);
	/**
	 * Retrieve multiple properties by the index field name they serve as a label
	 * @param itemFieldNames
	 * @return
	 */
	List<PropertyType> findByItemFieldNamesIn(Set<String> itemFieldNames);
	/**
	 * Retrieve multiple fields by their local name OR the index field name 
	 * @param names
	 * @return
	 */
	@Query(fields={
			IPropertyType.TYPE_FIELD, 
			IPropertyType.IS_FACET_FIELD, 
			IPropertyType.BOOST_FIELD, 
			IPropertyType.USED_WITH_FIELD, 
			IPropertyType.IDX_FIELD_NAME_FIELD,
			IPropertyType.PROPERTY_TYPE_FIELD, 
			IPropertyType.LABEL_FIELD, 
			IPropertyType.ALTERNATE_LABEL_FIELD, 
			IPropertyType.HIDDEN_LABEL_FIELD, 
			IPropertyType.LANGUAGES_FIELD,
			IPropertyType.LANGUAGE_TXT_FIELD,
			IPropertyType.LOCAL_NAME_FIELD, 
			IPropertyType.NAME_SPACE_FIELD, 
			IPropertyType.ID_FIELD, 
			IPropertyType.COMMENT_FIELD, 
			IPropertyType.DESCRIPTION_FIELD, 
			IPropertyType.RANGE_FIELD,
			IPropertyType.VALUE_QUALIFIER_FIELD
		})
	List<PropertyType> findByLocalNameOrItemFieldNamesIn(Set<String> names);
	/**
	 * Remove all properties of the provided namespace
	 * @param namespace
	 * @return
	 */
	long deleteByNameSpace(String namespace);

}
