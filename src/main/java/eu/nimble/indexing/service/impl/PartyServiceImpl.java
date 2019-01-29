package eu.nimble.indexing.service.impl;

import org.springframework.stereotype.Service;

import eu.nimble.indexing.service.PartyService;
import eu.nimble.service.model.solr.party.PartyType;

@Service
public class PartyServiceImpl extends SolrServiceImpl<PartyType> implements PartyService {

	@Override
	public String getCollection() {
		return PartyType.COLLECTION;
	}

	@Override
	public Class<PartyType> getSolrClass() {
		return PartyType.class;
	}


}
