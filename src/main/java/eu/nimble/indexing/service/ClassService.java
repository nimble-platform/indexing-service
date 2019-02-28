package eu.nimble.indexing.service;

import java.util.Set;

import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.ClassType;

public interface ClassService extends SolrService<ClassType> {
	
	public SearchResult<ClassType> findByProperty(String property);
	
	public SearchResult<ClassType> findByUris(Set<String> uriSet);
	
	public SearchResult<ClassType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames);

}
