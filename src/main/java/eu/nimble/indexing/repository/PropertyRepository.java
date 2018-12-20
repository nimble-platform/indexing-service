package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.owl.Property;

@Repository
public interface PropertyRepository  extends SolrCrudRepository<Property, String>{

	@Query(fields= {"*"})
	List<Property> findByUri(String uri);
	@Query(fields= {"*"})
	List<Property> findByProduct(String product);
	
	List<Property> findByProductAndLanguages(String product, String language);
//	@Query(fields= {"*", "[child parentFilter=type:property childFilter=lang:?1]"})
//	List<Property> findByUsedInAndLang(List<String> usedIn, String language);
//	@Query(fields= {"*", "[child parentFilter=type:property]"})
//	List<Property> findByUsedIn(List<String> usedIn);
}
