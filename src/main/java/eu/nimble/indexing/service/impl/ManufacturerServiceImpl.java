package eu.nimble.indexing.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.Query;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.ManufacturerPartyRepository;
import eu.nimble.indexing.repository.model.catalogue.PartyType;
import eu.nimble.indexing.service.ManufacturerService;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {

	private ManufacturerPartyRepository repo;
	@Override
	public PartyType getManufacturerParty(String uri) {
		return repo.findById(uri).orElse(null);
	}

	@Override
	public void setManufacturerParty(PartyType prop) {
		repo.save(prop);
		
	}

	@Override
	public void removeRemoveManufacturerParty(String uri) {
		PartyType p = getManufacturerParty(uri);
		if (p!=null) {
			repo.delete(p);
		}
		
	}

	@Override
	public List<PartyType> getManufacturerParties(Query forProperty) {
		// TODO Auto-generated method stub
		return new ArrayList<PartyType>();
	}
	
	@Autowired
	public void setManufacturerPartyRepository(ManufacturerPartyRepository repository) {
		this.repo = repository;
	}
	

}
