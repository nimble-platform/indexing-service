package eu.nimble.indexing.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.catalogue.PartyType;

@Repository
public interface ManufacturerPartyRepository extends SolrCrudRepository<PartyType, String> {

}
