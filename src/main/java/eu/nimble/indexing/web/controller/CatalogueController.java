package eu.nimble.indexing.web.controller;

import java.util.List;

import eu.nimble.service.model.solr.owl.ClassType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "Catalogue Controller",
		description = "Catalogue API to index and delete catalogues (list of items)")
public class CatalogueController {
	@Autowired
	private ItemService items;

	@ApiOperation(value = "", notes = "Delete all items in a catalogue", response = Long.class)
	@DeleteMapping("/catalogue")
	public ResponseEntity<Long> deleteCatalogue(
			@RequestParam(value="catalogueId") String catalogueId
			) {
		return ResponseEntity.ok(items.deleteCatalogue(catalogueId));
	}

	@ApiOperation(value = "", notes = "Index a list of items", response = Boolean.class)
	@PostMapping("/catalogue")
	public ResponseEntity<Boolean> postCatalogue(
			@RequestParam(value="catalogueId") String catalogueId,
			@RequestBody List<ItemType> catalogueItems) {
		return ResponseEntity.ok(items.set(catalogueItems));
	}    
}
