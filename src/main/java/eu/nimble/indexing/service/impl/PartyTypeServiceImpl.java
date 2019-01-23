package eu.nimble.indexing.service.impl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.PartyTypeRepository;
import eu.nimble.indexing.service.PartyTypeService;
import eu.nimble.service.model.solr.party.PartyType;

@Service
public class PartyTypeServiceImpl implements PartyTypeService {

	private PartyTypeRepository repo;
	
	@Override
	public PartyType getPartyType(String uri) {
		return repo.findById(uri).orElse(null);
	}
	@Override
	public Optional<PartyType> findById(String id) {
		return repo.findById(id);
	}

	@Override
	public void setPartyType(PartyType prop) {
		repo.save(prop);
		
	}

	@Override
	public void removePartyType(String uri) {
		PartyType p = getPartyType(uri);
		if (p!=null) {
			repo.delete(p);
		}
		
	}


	@Autowired
	public void setPartyTypeRepository(PartyTypeRepository repository) {
		this.repo = repository;
	}

	@Override
	public Map<String, PartyType> getPartyTypes(Set<String> identifiers) {
		// construct a map for easier access
		return repo.findByIdIn(identifiers)
				.stream()
				.collect(Collectors.toMap(PartyType::getId, c -> c));

	}
	

}
