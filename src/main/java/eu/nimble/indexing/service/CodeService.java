package eu.nimble.indexing.service;

import java.util.Set;

import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.CodedType;

public interface CodeService extends SolrService<CodedType> {
	
	public SearchResult<CodedType> findByListId(String listId);
	
	public SearchResult<CodedType> findByListIdAndCode(String listId, String code);
	
	public SearchResult<CodedType> findByUris(Set<String> uriSet);
	
	public SearchResult<CodedType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames);

}
