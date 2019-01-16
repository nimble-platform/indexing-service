package eu.nimble.indexing.repository;

import java.util.List;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import eu.nimble.indexing.repository.model.catalogue.PartyType;

@Repository
public interface PartyTypeRepository extends SolrCrudRepository<PartyType, String> {
	/**
	 * Retrieve all manufacturers as provided in the list
	 * @param id
	 * @return
	 */
	List<PartyType> findByIdIn(List<String> id);

}
