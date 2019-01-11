package eu.nimble.indexing.service;

import java.util.List;

import eu.nimble.indexing.repository.model.owl.Property;

public interface PropertyService {
	
	public Property getProperty(String uri);
	
	public void setProperty(Property prop);
	
	public void removeProperty(String uri);

	public List<Property> getProperties(String forClass);
	
	public List<Property> getPropertiesByName(List<String> names);
	
	public List<Property> getPropertiesByUri(List<String> uri);
	

}
