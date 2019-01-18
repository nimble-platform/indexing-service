package eu.nimble.indexing.service;

import java.util.List;
import java.util.Set;

import eu.nimble.indexing.repository.model.owl.PropertyType;

public interface PropertyService {
	
	public PropertyType getProperty(String uri);
	
	public void setProperty(PropertyType prop);
	
	public void removeProperty(String uri);

	public List<PropertyType> getProperties(String forClass);
	
	public List<PropertyType> getPropertiesByName(String nameSpace, Set<String> names);
	
	public List<PropertyType> getPropertiesByIndexName(Set<String> names);
	
	public List<PropertyType> getPropertiesByUri(Set<String> uri);
	

}
