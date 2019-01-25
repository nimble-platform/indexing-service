package eu.nimble.indexing.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.ClassType;
import eu.nimble.service.model.solr.owl.PropertyType;

public interface PropertyService {
	
	public PropertyType getProperty(String uri);
	
	public void setProperty(PropertyType prop);
	
	public void removeProperty(String uri);
	
	public void removeByNamespace(String namespace);

	public List<PropertyType> getProperties(String forClass);
	
	public List<PropertyType> getPropertiesByName(String nameSpace, Set<String> names);
	
	public List<PropertyType> getPropertiesByIndexName(Set<String> names);
	
	public List<PropertyType> getPropertiesByUri(Set<String> uri);
	public SearchResult<PropertyType> search(String solrQuery, Pageable page);

	public SearchResult<PropertyType> search(String search, String language, Pageable page);

	public SearchResult<PropertyType> search(String search, String language, boolean labelsOnly, Pageable page);
	

}
