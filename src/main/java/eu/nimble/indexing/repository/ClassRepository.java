package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.owl.ClassType;

@Repository
public interface ClassRepository  extends SolrCrudRepository<ClassType, String>{

	
	@Query(fields= {"*"})
	List<ClassType> findByUri(String uri);
	@Query(fields= {"*"})
	List<ClassType> findByProperties(String properties);
	/**
	 * Remove all properties of the provided namespace
	 * @param namespace
	 * @return
	 */
	long deleteByNameSpace(String namespace);
}
