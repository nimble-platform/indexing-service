package eu.nimble.indexing.repository;

import org.springframework.data.domain.Pageable;

import eu.nimble.indexing.model.SearchResult;
import eu.nimble.indexing.repository.model.catalogue.ItemType;

public interface CustomizedItemRepository {
	SearchResult<ItemType> findItemByName(String name, String language, Pageable pageable);

}
