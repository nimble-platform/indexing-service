package eu.nimble.indexing.service.event;

import org.springframework.context.ApplicationEvent;

import eu.nimble.service.model.solr.party.PartyType;

public class ManufacturerEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	
	public ManufacturerEvent(Object source, PartyType party) {
		super(source);
		this.manufacturer = party;
	}
	private final PartyType manufacturer;
	
	public PartyType getManufacturer() {
		return manufacturer;
	}


}
