package eu.nimble.indexing.service;

import java.util.List;

import eu.nimble.service.model.solr.item.ItemType;

public interface ItemService extends SolrService<ItemType> {
	public long setCatalogue(String catalogueId, List<ItemType> items);
	public long deleteCatalogue(String catalogueId);
	
}
