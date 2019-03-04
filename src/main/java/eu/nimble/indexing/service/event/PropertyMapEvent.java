package eu.nimble.indexing.service.event;

import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationEvent;

public class PropertyMapEvent extends ApplicationEvent {

	public PropertyMapEvent(Object source, Map<String,Set<String>> custom) {
		super(source);

		this.fieldNames = custom;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Map<String, Set<String>> fieldNames;
	
	public Map<String, Set<String>> getFieldNames() {
		return fieldNames;
	}
	
	
	

}
