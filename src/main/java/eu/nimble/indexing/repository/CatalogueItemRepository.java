package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import eu.nimble.indexing.repository.model.catalogue.ItemType;

public interface CatalogueItemRepository extends CatalogueItemBaseRepository, SolrCrudRepository<ItemType, String>{
	String PARENT_FILTER = "[child parentFilter=doctype:item]";
	@Query(fields= {"*", PARENT_FILTER})
	ItemType findByUri(String uri);
	
	
	@Query(value="label_?1:?0*", fields= {"*", PARENT_FILTER})
	public List<ItemType> findByNameStartingWith(String name, String lang);
	
}
