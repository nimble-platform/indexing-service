package eu.nimble.indexing.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.ClassType;

public interface ClassService {
	
	public ClassType getClass(String uri);
	
	public void setClass(ClassType prop);
	
	public void removeClass(String uri);

	public List<ClassType> getClassesForProperty(String forProperty);

	List<ClassType> getClasses(Set<String> uri);
	
	List<ClassType> getClassesForLocalNames(String nameSpace, Set<String> localNames);
	
	List<ClassType> search(String solrQuery);

	public SearchResult<ClassType> search(String query, String lang, boolean labelsOnly, Pageable page);
}
