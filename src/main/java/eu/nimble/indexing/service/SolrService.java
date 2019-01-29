package eu.nimble.indexing.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;

import eu.nimble.service.model.solr.IndexField;
import eu.nimble.service.model.solr.SearchResult;

public interface SolrService<T> {
	
	/**
	 * Obtain an item based on it's identifier
	 * @param uri
	 * @return
	 */
	public Optional<T> get(String uri); 
	/**
	 * Store an item
	 * @param item
	 */
	public void set(T item);
	/**
	 * Store a list of items
	 * @param items
	 */
	public boolean set(List<T> items);
	/**
	 * Delete an item from the index
	 * @param uri
	 */
	public void remove(String uri);

	public Collection<IndexField> fields();
	
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, Pageable page);
	
	public SearchResult<T> select(Criteria query, List<String> filterQueries, List<String> facetFields, Pageable page);

}
