package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.owl.PropertyType;

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
	 * Retrieve all properties for a distinct product which a label in the 
	 * desired language
	 * @param product The product's uri
	 * @param language The language code such as <code>en</code> ...
	 * @return
	 */
	List<PropertyType> findByProductAndLanguages(String product, String language);
	/**
	 * Retrieve multiple properties by their localName
	 * @param names The list of localName's (without namespace)
	 * @return
	 */
	List<PropertyType> findByLocalNameIn(List<String> names);
	/**
	 * Retrieve multiple properties by their uri
	 * @param uri list of URI's to resolve
	 * @return
	 */
	List<PropertyType> findByUriIn(List<String> uri);
	/**
	 * Remove all properties of the provided namespace
	 * @param namespace
	 * @return
	 */
	long deleteByNameSpace(String namespace);

}
