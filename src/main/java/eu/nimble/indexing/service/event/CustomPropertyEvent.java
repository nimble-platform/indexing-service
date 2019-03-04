package eu.nimble.indexing.service.event;

import org.springframework.context.ApplicationEvent;

import eu.nimble.service.model.solr.item.ItemType;

public class CustomPropertyEvent extends ApplicationEvent {

	public CustomPropertyEvent(Object source, ItemType custom) {
		super(source);

		this.item = custom;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ItemType item;
	
	public ItemType getItem() {
		return item;
	}
	
	

}
