package eu.nimble.indexing.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.service.OntologyService;

@RestController
public class OntologyController {
	
	@Autowired
	private OntologyService onto;
			
	@PostMapping("/ontology")
    public ResponseEntity<Void> uploadCatalogue(
    		@RequestHeader(value = "Authorization") String bearerToken,
    		@RequestHeader(value = "Content-Type") String mimeType,
    		@RequestBody String content) {
		System.out.println(mimeType);

		onto.upload(mimeType, content);
    	return ResponseEntity.ok(null);
    }

}
