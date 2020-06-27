package eu.nimble.indexing.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import eu.nimble.service.model.solr.FacetResult;
import eu.nimble.service.model.solr.IndexField;
import eu.nimble.service.model.solr.Search;
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
	 * Clear the index
	 */
	public void clearIndex();
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
	/**
	 * Retrieve the {@link IndexField} descritors for the collection. Perform
	 * filtering based on the provided list. 
	 * @param fields A list of fieldNames to return, wildcards are allowed as the first/last character. 
	 * @return
	 */
	public Collection<IndexField> fields(Set<String> fields);
	/**
	 * Perform a search against the collection
	 * @param search The search definition holding query, filterQuery and faceting parameters
	 * @return
	 */
	public SearchResult<T> search(Search search);
	/**
	 * Perform a select query against the collection
	 * @param query The query term used with the <code>q</code> query parameter
	 * @param filterQueries The filter terms uses as <code>fq</code> query parameters
	 * @param facetFields The names used for faceting, e.g. <code>facet.field</code> parameters
	 * @param facetLimit The number of facet elements to return for each facet
	 * @param facetMinCount The minimum number of facet occurrences to be included in the result
	 * @param page The {@link Pageable} pointing to the current page & size
	 * @return
	 */
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, int facetLimit, int facetMinCount, Pageable page);

	/**
	 * Perform a select query against the collection
	 * @param query The query term used with the <code>q</code> query parameter
	 * @param filterQueries The filter terms uses as <code>fq</code> query parameters
	 * @param facetFields The names used for faceting, e.g. <code>facet.field</code> parameters
	 * @param sortFields  Set of attributes for search pointing to the current page & size
	 * @param facetLimit The number of facet elements to return for each facet
	 * @param facetMinCount The minimum number of facet occurrences to be included in the result
	 * @param page The {@link Pageable} pointing to the current page & size
	 * @return
	 */
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields,List<String> sortFields, int facetLimit, int facetMinCount, Pageable page);

	/**
	 * Perform a auto suggest query
	 * @param query The query term used with the <code>q</code> query parameter
	 * @param filterQueries The filter terms uses as <code>fq</code> query parameters
	 * @param facetFields The names used for faceting, e.g. <code>facet.field</code> parameters
	 * @param facetLimit The number of facet elements to return for each facet
	 * @param facetMinCount The minimum number of facet occurrences to be included in the result
	 * @return A {@link FacetResult} denoting name, facet and facet count
	 */
	public FacetResult suggest(String query, String facetField, int facetLimit, int facetMinCount);
}
