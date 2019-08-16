package eu.nimble.indexing.web.controller;

import java.util.List;

import eu.nimble.indexing.service.IdentityService;
import eu.nimble.service.model.solr.owl.ClassType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static eu.nimble.indexing.utils.OpenIdConnectUserDetails.Role.*;

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
	private IdentityService identityService;

	@Autowired
	private ItemService items;

	@ApiOperation(value = "", notes = "Delete all items in a catalogue", response = Long.class)
	@DeleteMapping("/catalogue")
	public ResponseEntity<?> deleteCatalogue(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(value="catalogueId") String catalogueId
			)  throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE, PUBLISHER
				,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		return ResponseEntity.ok(items.deleteCatalogue(catalogueId));
	}

	@ApiOperation(value = "", notes = "Index a list of items", response = Boolean.class)
	@PostMapping("/catalogue")
	public ResponseEntity<?> postCatalogue(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(value="catalogueId") String catalogueId,
			@RequestBody List<ItemType> catalogueItems) {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE, PUBLISHER
				,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		return ResponseEntity.ok(items.set(catalogueItems));
	}    
}
