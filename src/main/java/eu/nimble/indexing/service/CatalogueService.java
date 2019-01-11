package eu.nimble.indexing.service;

import java.util.Collection;

import org.springframework.data.domain.Pageable;

import eu.nimble.indexing.model.IndexField;
import eu.nimble.indexing.model.SearchResult;
import eu.nimble.indexing.repository.model.catalogue.ItemType;

public interface CatalogueService {
	/**
	 * Obtain an item based on it's identifier
	 * @param uri
	 * @return
	 */
	public ItemType getItem(String uri); 
	/**
	 * Store an item
	 * @param item
	 */
	public void setItem(ItemType item);
	/**
	 * Delete an item from the index
	 * @param uri
	 */
	public void removeItem(String uri);
	/**
	 * search an item
	 * @param query
	 * @param pageable
	 * @return
	 */
	public SearchResult<ItemType> search(String query, Pageable pageable);
	
	public Collection<IndexField> fieldsInUse();
}
