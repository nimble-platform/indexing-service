package eu.nimble.indexing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import eu.nimble.indexing.repository.model.catalogue.ItemType;

public interface CatalogueItemBaseRepository {
	
	Page<ItemType> findItemByName(String name, String language, Pageable pageable);

	
}
