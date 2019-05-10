package eu.nimble.indexing.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.service.model.solr.owl.CodedType;

@Repository
public interface CodedRepository  extends SolrCrudRepository<CodedType, String>{
	/**
	 * Find coded elements having the specified code
	 * @param properties
	 * @return
	 */
	List<CodedType> findByCodeIn(Set<String> codes);
	/**
	 * Find coded elements based on their list assignment
	 * @param listId
	 * @return
	 */
	List<CodedType> findByListId(String listId);
	/**
	 * Find coded elements based on their list assignment
	 * @param listId
	 * @return
	 */
	List<CodedType> findByListIdAndCode(String listId, String code);
	/**
	 * Delete all codes in the given lists
	 * @param listId set
	 * @return
	 */
	long deleteByListIdIn(Set<String> listId);
	/**
	 * Retrieve all classes based on their id (uri)
	 * @param uri
	 * @return
	 */
	List<CodedType> findByUriIn(Set<String> uri);
	/**
	 * 
	 * @param namespace
	 * @param localNames
	 * @return
	 */
	List<CodedType> findByNameSpaceAndLocalNameIn(String namespace, Set<String> localNames);
}


