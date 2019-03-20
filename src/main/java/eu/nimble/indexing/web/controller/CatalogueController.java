package eu.nimble.indexing.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.service.ItemService;
import eu.nimble.service.model.solr.item.ItemType;
/**
 * Controller for managing catalogs. Supports
 * <ul>
 * <li>bulk uploading of items belonging to a single catalogue
 * <li>deletion of all items in the given catalogue
 * </ul> 
 * @author dglachs
 *
 */
@RestController
public class CatalogueController {
	@Autowired
	private ItemService items;


	@DeleteMapping("/catalogue")
	public ResponseEntity<Long> deleteCatalogue(
			@RequestParam(value="catalogueId") String catalogueId
			) {
		return ResponseEntity.ok(items.deleteCatalogue(catalogueId));
	}
	@PostMapping("/catalogue")
	public ResponseEntity<Boolean> postCatalogue(
			@RequestParam(value="catalogueId") String catalogueId,
			@RequestBody List<ItemType> catalogueItems) {
		return ResponseEntity.ok(items.set(catalogueItems));
	}    
}
