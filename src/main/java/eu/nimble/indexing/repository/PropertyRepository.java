package eu.nimble.indexing.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

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
	List<PropertyType> findByProduct(String product);
	/**
	 * 
	 * @param namespace
	 * @param localNames
	 * @return
	 */
	List<PropertyType> findByNameSpaceAndLocalNameIn(String namespace, Set<String> localNames);
	/**
	 * Retrieve multiple properties by their uri
	 * @param uri list of URI's to resolve
	 * @return
	 */
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
	List<PropertyType> findByLocalNameOrItemFieldNamesIn(List<String> names);
	/**
	 * Remove all properties of the provided namespace
	 * @param namespace
	 * @return
	 */
	long deleteByNameSpace(String namespace);

}
