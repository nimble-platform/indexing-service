package eu.nimble.indexing.service;

import java.util.List;

import eu.nimble.indexing.repository.model.owl.Clazz;

public interface ClassService {
	
	public Clazz getClass(String uri);
	
	public void setClass(Clazz prop);
	
	public void removeClass(String uri);

	public List<Clazz> getClasses(String forProperty);

}
