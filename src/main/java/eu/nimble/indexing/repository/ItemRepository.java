package eu.nimble.indexing.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.service.model.solr.item.ItemType;

@Repository
public interface ItemRepository extends SolrCrudRepository<ItemType, String>, CustomizedItemRepository {
	/**
	 * Delete the items of the provided catalouge
	 * @param catalogueId
	 * @return
	 */
	public long deleteByCatalogueId(String catalogueId);

}
