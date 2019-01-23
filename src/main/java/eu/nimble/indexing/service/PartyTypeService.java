package eu.nimble.indexing.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import eu.nimble.service.model.solr.party.PartyType;

public interface PartyTypeService {
	
	public PartyType getPartyType(String uri);
	
	public void setPartyType(PartyType prop);
	
	public void removePartyType(String uri);

	/**
	 * Retrieve a map with the {@link PartyType#getId()} as key
	 * @param identifiers A list of id's to search
	 * @return The constructed map
	 */
	public Map<String, PartyType> getPartyTypes(Set<String> identifiers);

	Optional<PartyType> findById(String id);

}
