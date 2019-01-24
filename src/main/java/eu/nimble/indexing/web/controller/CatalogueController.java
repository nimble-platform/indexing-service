package eu.nimble.indexing.web.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.model.IndexField;
import eu.nimble.indexing.model.SearchResult;
import eu.nimble.indexing.service.CatalogueService;
import eu.nimble.service.model.solr.item.ItemType;

@RestController
public class CatalogueController {
	@Autowired
	private CatalogueService items;


	@DeleteMapping("/catalogue")
	public ResponseEntity<Long> deleteCatalogue(
			@RequestParam(value="catalogueId") String catalogueId
			) {
		return ResponseEntity.ok(items.deleteCatalogue(catalogueId));
	}
	@PostMapping("/catalogue")
	public ResponseEntity<Boolean> postCatalogue(
			@RequestParam(value="catalogueId") String catalogueId,
			List<ItemType> catalogueItems) {
		return ResponseEntity.ok(items.setItems(catalogueItems));
	}
    @GetMapping("/catalogue/items")
    public ResponseEntity<SearchResult<ItemType>> search(
//    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestParam(value="q") String query
    		) {
    	SearchResult<ItemType> result = items.search(query, new SolrPageRequest(0, 10));
    	return ResponseEntity.ok(result);
    }
    
    @GetMapping("/catalogue/fields")
    public ResponseEntity<Collection<IndexField>> obtainFieldsInUse(
//    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestParam(value="q", required=false) String query
    		) {
    	Collection<IndexField> result = items.fieldsInUse(); //(query, new SolrPageRequest(0, 10));
    	return ResponseEntity.ok(result);
    }
}
