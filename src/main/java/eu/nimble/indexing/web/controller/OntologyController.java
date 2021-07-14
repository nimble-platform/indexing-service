package eu.nimble.indexing.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.nimble.indexing.service.IdentityService;
import eu.nimble.indexing.utils.SearchEvent;
import eu.nimble.utility.LoggerUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static eu.nimble.indexing.utils.OpenIdConnectUserDetails.Role.*;


import eu.nimble.indexing.service.OntologyService;

@RestController
@Api(value = "Ontology Indexing API", description = "Controller to upload and delete ontologies in the index")
public class OntologyController {
	
	@Autowired
	private OntologyService onto;

	@Autowired
	private IdentityService identityService;

	private static final Logger logger = LoggerFactory.getLogger(OntologyController.class);

	@ApiOperation(value="", notes = "Upload an ontology to the index")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Ontology successfully indexed"),
			@ApiResponse(code = 400, message = "Error while ontology indexing")})
	@PostMapping("/ontology")
	public ResponseEntity<?> uploadOntology(
    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestHeader(value = "Content-Type", required=false) String mimeType,
    		@RequestParam(name="nameSpace") List<String> nameSpace,
    		@RequestBody String content) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER) == false)
			return new ResponseEntity<>("Only platform managers are allowed to update ontology",
					HttpStatus.UNAUTHORIZED);

		//mdc logging
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("activity", SearchEvent.INDEX_ONTOLOGY.getActivity());
		LoggerUtils.logWithMDC(logger, paramMap, LoggerUtils.LogLevel.INFO, "Indexing an ontology with mime-type: {}", mimeType);
		// nameSpace must not be null
		if ( nameSpace == null) {
			nameSpace = new ArrayList<String>();
		}
		onto.upload(mimeType, nameSpace, content);
		logger.info("Indexed the ontologies with namespaces {}",nameSpace);
    	return ResponseEntity.ok(null);
    }


	@ApiOperation(value="", notes= "Delete an ontology to the index")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Ontology successfully deleted"),
			@ApiResponse(code = 400, message = "Error while ontology deleting")})
    @DeleteMapping("/ontology")
    public ResponseEntity<?> deleteOntology(
    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestParam(name="nameSpace") String nameSpace) throws Exception {
		//mdc logging
		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER) == false)
			return new ResponseEntity<>("Only platform managers are allowed to delete ontology",
					HttpStatus.UNAUTHORIZED);

		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("activity", SearchEvent.DELETE_ONTOLOGY.getActivity());
		LoggerUtils.logWithMDC(logger, paramMap, LoggerUtils.LogLevel.INFO, "Indexing an ontology with namespace: {}", nameSpace);
		onto.deleteNamespace(nameSpace);
		logger.info("Deleted the ontology with namespace {}",nameSpace);
    	return ResponseEntity.ok(null);
	}

}
