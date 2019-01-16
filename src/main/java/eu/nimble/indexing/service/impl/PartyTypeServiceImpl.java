package eu.nimble.indexing.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.Query;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.PartyTypeRepository;
import eu.nimble.indexing.repository.model.catalogue.PartyType;
import eu.nimble.indexing.service.PartyTypeService;

@Service
public class PartyTypeServiceImpl implements PartyTypeService {

	private PartyTypeRepository repo;
	
	@Override
	public PartyType getPartyType(String uri) {
		return repo.findById(uri).orElse(null);
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

	@Override
	public List<PartyType> getPartyTypes(Query forProperty) {
		// TODO Auto-generated method stub
		return new ArrayList<PartyType>();
	}
	
	@Autowired
	public void setPartyTypeRepository(PartyTypeRepository repository) {
		this.repo = repository;
	}
	

}
