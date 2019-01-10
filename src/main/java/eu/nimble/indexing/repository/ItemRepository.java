package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.catalogue.ItemType;

@Repository
public interface ItemRepository extends SolrCrudRepository<ItemType, String>, CustomizedItemRepository {
	String PARENT_FILTER = "[child parentFilter=doctype:item]";
	@Query(fields= {"*", PARENT_FILTER})
	ItemType findByUri(String uri);
	
	
	@Query(value="label_?1:?0*", fields= {"*", PARENT_FILTER})
	public List<ItemType> findByNameStartingWith(String name, String lang);

}
