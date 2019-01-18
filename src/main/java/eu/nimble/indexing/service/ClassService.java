package eu.nimble.indexing.service;

import java.util.List;
import java.util.Set;

import eu.nimble.indexing.repository.model.owl.ClassType;

public interface ClassService {
	
	public ClassType getClass(String uri);
	
	public void setClass(ClassType prop);
	
	public void removeClass(String uri);

	public List<ClassType> getClassesForProperty(String forProperty);

	List<ClassType> getClasses(Set<String> uri);
	
	List<ClassType> getClassesForLocalNames(String nameSpace, Set<String> localNames);
	

}
