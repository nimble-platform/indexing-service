package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.owl.Property;

@Repository
public interface PropertyRepository  extends SolrCrudRepository<Property, String>{
	/**
	 * Obtain a single property by it's uri
	 * @param uri
	 * @return
	 */
	List<Property> findByUri(String uri);
	/**
	 * Retrieve all properties for a distinct product
	 * @param product The product's uri
	 * @return
	 */
	List<Property> findByProduct(String product);
	/**
	 * Retrieve all properties for a distinct product which a label in the 
	 * desired language
	 * @param product The product's uri
	 * @param language The language code such as <code>en</code> ...
	 * @return
	 */
	List<Property> findByProductAndLanguages(String product, String language);
	/**
	 * Retrieve multiple properties by their localName
	 * @param names The list of localName's (without namespace)
	 * @return
	 */
	List<Property> findByLocalNameIn(List<String> names);
	/**
	 * Retrieve multiple properties by their uri
	 * @param uri list of URI's to resolve
	 * @return
	 */
	List<Property> findByUriIn(List<String> uri);
}
