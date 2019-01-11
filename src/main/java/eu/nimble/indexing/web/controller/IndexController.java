package eu.nimble.indexing.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.model.IndexField;
import eu.nimble.indexing.repository.model.ItemUtils;
import eu.nimble.indexing.repository.model.PartyTypeUtils;
import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.repository.model.catalogue.PartyType;
import eu.nimble.indexing.repository.model.owl.Clazz;
import eu.nimble.indexing.repository.model.owl.Property;
import eu.nimble.indexing.service.CatalogueService;
import eu.nimble.indexing.service.ClassService;
import eu.nimble.indexing.service.ManufacturerService;
import eu.nimble.indexing.service.PropertyService;

@RestController
public class IndexController {
	
	@Autowired
	private PropertyService properties;
	
	@Autowired
	private ClassService classes;
	
	@Autowired
	private ManufacturerService manufacturers;
	
	@Autowired
	private CatalogueService items;

	@GetMapping("/class")
	public ResponseEntity<Clazz> getClass(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		Clazz c = classes.getClass(uri);
		return ResponseEntity.ok(c);
	}
	@GetMapping("/classes")
	public ResponseEntity<List<Clazz>> getClasses(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String property) {
		List<Clazz> result = classes.getClasses(property);
		return ResponseEntity.ok(result);
	}
	@DeleteMapping("/class")
	public ResponseEntity<Boolean> removeClass(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		classes.removeClass(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/manufacturer")
	public ResponseEntity<PartyType> getManufacturer(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam(defaultValue="null") String uri) {
		if ( uri.equals("null") ) {
			return ResponseEntity.ok(PartyTypeUtils.template());
		}
		PartyType m = manufacturers.getManufacturerParty(uri);
		return ResponseEntity.ok(m);
	}
	@GetMapping("/manufacturers")
	public ResponseEntity<List<PartyType>> getManufacturers(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String property) {
		// TODO: check query options
		List<PartyType> result = manufacturers.getManufacturerParties(null);
		return ResponseEntity.ok(result);
	}
	@DeleteMapping("/manufacturer")
	public ResponseEntity<Boolean> removeManufacturer(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		manufacturers.removeRemoveManufacturerParty(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	@PostMapping("/manufacturer")
	public ResponseEntity<Boolean> setManufacturer(		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody PartyType party) {
		manufacturers.setManufacturerParty(party);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/property")
	public ResponseEntity<Property> getProperty(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		Property prop = properties.getProperty(uri);
		return ResponseEntity.ok(prop);
	}
	@GetMapping("/properties")
	public ResponseEntity<List<Property>> getProperties(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam(name="product", required=false) String productType,
			@RequestParam(name="localName", required=false) List<String> localNames) {
		if ( productType != null) {
			List<Property> prop = properties.getProperties(productType);
			return ResponseEntity.ok(prop);
		}
		if ( localNames != null) {
			List<Property> prop = properties.getPropertiesByName(localNames);
			return ResponseEntity.ok(prop);
			
		}
		return ResponseEntity.ok(new ArrayList<>());
	}
	@DeleteMapping("/property")
	public ResponseEntity<Boolean> removeProperty(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		properties.removeProperty(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/property")
	public ResponseEntity<Boolean> setProperty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody Property prop
    		) {
		properties.setProperty(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	

	@GetMapping("/item")
	public ResponseEntity<ItemType> getItem(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam(defaultValue="null") String uri) {
		if ( uri.equals("null")) {
			return ResponseEntity.ok(ItemUtils.template());
		}
		ItemType prop = items.getItem(uri);
		return ResponseEntity.ok(prop);
	}

	@DeleteMapping("/item")
	public ResponseEntity<Boolean> removeItem(    		
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestParam String uri) {
		items.removeItem(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/item")
	public ResponseEntity<Boolean> setItem(
//			@RequestHeader(value = "Authorization") String bearerToken, 
    		@RequestBody ItemType prop
    		) {
		items.setItem(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

}
