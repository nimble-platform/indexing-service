package eu.nimble.indexing.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.service.OntologyService;

@RestController
public class OntologyController {
	
	@Autowired
	private OntologyService onto;

	private static final Logger logger = LoggerFactory.getLogger(OntologyController.class);


	@PostMapping("/ontology")
    public ResponseEntity<Void> uploadOntology(
//    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestHeader(value = "Content-Type", required=false) String mimeType,
    		@RequestParam(name="nameSpace") List<String> nameSpace,
    		@RequestBody String content) {
		logger.info("Indexing an ontology with mime-type : " + mimeType);
		// nameSpace must not be null
		if ( nameSpace == null) {
			nameSpace = new ArrayList<String>();
		}
		onto.upload(mimeType, nameSpace, content);
    	return ResponseEntity.ok(null);
    }
	
	@DeleteMapping("/ontology")
    public ResponseEntity<Void> deleteOntology(
//    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestParam(name="nameSpace") String nameSpace) {

		onto.deleteNamespace(nameSpace);
    	return ResponseEntity.ok(null);
	}

}
