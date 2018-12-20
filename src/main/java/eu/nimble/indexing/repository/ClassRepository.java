package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.owl.Clazz;

@Repository
public interface ClassRepository  extends SolrCrudRepository<Clazz, String>{

	
	@Query(fields= {"*"})
	List<Clazz> findByUri(String uri);
	@Query(fields= {"*"})
	List<Clazz> findByProperties(String properties);
}
