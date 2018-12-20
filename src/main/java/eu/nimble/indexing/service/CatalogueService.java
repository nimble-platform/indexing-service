package eu.nimble.indexing.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;

import eu.nimble.indexing.repository.model.catalogue.ItemType;

public interface CatalogueService {
	
	public ItemType getItem(String uri); 

	public void setItem(ItemType item);
	
	public void removeItem(String uri);
	
	public FacetPage<ItemType> search(String query, Pageable pageable);
}
