package eu.nimble.indexing.web.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
