package eu.nimble.indexing.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.service.model.solr.owl.ClassType;

@Repository
public interface ClassRepository  extends SolrCrudRepository<ClassType, String>{
	/**
	 * Find classes having the specified property
	 * @param properties
	 * @return
	 */
	List<ClassType> findByProperties(String properties);
	/**
	 * Remove all classes of the provided namespace
	 * @param namespace
	 * @return
	 */
	long deleteByNameSpace(String namespace);
	/**
	 * Delete all classes of the provided namespaces
	 * @param namespaces
	 * @return
	 */
	long deleteByNameSpaceIn(Set<String> namespaces);
	/**
	 * Retrieve all classes based on their id (uri)
	 * @param uri
	 * @return
	 */
	List<ClassType> findByUriIn(Set<String> uri);
	/**
	 * 
	 * @param namespace
	 * @param localNames
	 * @return
	 */
	List<ClassType> findByNameSpaceAndLocalNameIn(String namespace, Set<String> localNames);
}


