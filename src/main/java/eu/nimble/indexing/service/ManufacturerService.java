package eu.nimble.indexing.service;

import java.util.List;

import org.springframework.data.solr.core.query.Query;

import eu.nimble.indexing.repository.model.catalogue.PartyType;

public interface ManufacturerService {
	
	public PartyType getManufacturerParty(String uri);
	
	public void setManufacturerParty(PartyType prop);
	
	public void removeRemoveManufacturerParty(String uri);

	public List<PartyType> getManufacturerParties(Query forProperty);

}
