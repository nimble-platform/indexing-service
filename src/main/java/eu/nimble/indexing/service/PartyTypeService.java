package eu.nimble.indexing.service;

import java.util.List;

import org.springframework.data.solr.core.query.Query;

import eu.nimble.indexing.repository.model.catalogue.PartyType;

public interface PartyTypeService {
	
	public PartyType getPartyType(String uri);
	
	public void setPartyType(PartyType prop);
	
	public void removePartyType(String uri);

	public List<PartyType> getPartyTypes(Query forProperty);

}
