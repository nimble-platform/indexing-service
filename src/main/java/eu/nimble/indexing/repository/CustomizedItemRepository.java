package eu.nimble.indexing.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;

import eu.nimble.indexing.repository.model.catalogue.ItemType;

public interface CustomizedItemRepository {
	FacetPage<ItemType> findItemByName(String name, String language, Pageable pageable);

}
