package eu.nimble.indexing.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.catalogue.AdditionalProperty;

@Repository
public interface AdditionalPropertyRepository extends SolrCrudRepository<AdditionalProperty, String> {

}
