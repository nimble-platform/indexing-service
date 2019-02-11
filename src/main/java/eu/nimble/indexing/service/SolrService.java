package eu.nimble.indexing.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	/**
	 * Retrieve the {@link IndexField} descriptors for the
	 * collection. The {@link IndexField} denotes
	 * <ul>
	 * <li>fieldName: as used in the collection
	 * <li>dataType: one of string, int, double, boolean
	 * <li>docCount: the number of documents containing this field
	 * <li>dynamicBase: the dynamic field used for deriving the fieldName
	 * </ul>
	 * @return
	 */
	public Collection<IndexField> fields();
	public Collection<IndexField> fields(Set<String> fields);
	/**
	 * Perform a select query against the collection
	 * @param query The query term used with the <code>q</code> query parameter
	 * @param filterQueries The filter terms uses as <code>fq</code> query parameters
	 * @param facetFields The names used for faceting, e.g. <code>facet.field</code> parameters
	 * @param page The pageable pointing to the current page & size
	 * @return
	 */
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, int facetLimit, Pageable page);
	
	public SearchResult<T> select(Criteria query, List<String> filterQueries, List<String> facetFields, int facetLimit, Pageable page);

}
