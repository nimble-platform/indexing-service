package eu.nimble.indexing.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.service.CatalogueService;

@RestController
public class CatalogueController {
	@Autowired
	private CatalogueService items;

	
    @GetMapping("/search/items")
    public ResponseEntity<FacetPage<ItemType>> search(
    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestParam(value="q") String query
    		) {
    	FacetPage<ItemType> result = items.search(query, new SolrPageRequest(0, 10));
    	return ResponseEntity.ok(result);
    }
}
