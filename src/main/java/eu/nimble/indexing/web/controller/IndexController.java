package eu.nimble.indexing.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.service.ClassService;
import eu.nimble.indexing.service.ItemService;
import eu.nimble.indexing.service.PartyService;
import eu.nimble.indexing.service.PropertyService;
import eu.nimble.indexing.utils.ItemUtils;
import eu.nimble.indexing.utils.PartyTypeUtils;
import eu.nimble.service.model.solr.IndexField;
import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.ClassType;
import eu.nimble.service.model.solr.owl.PropertyType;
import eu.nimble.service.model.solr.party.PartyType;

@RestController
public class IndexController {

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private ClassService classService;

	@Autowired
	private PartyService partyService;

	@Autowired
	private ItemService itemService;

	@GetMapping("/class")
	public ResponseEntity<ClassType> getClass(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String uri) {
		Optional<ClassType> c = classService.get(uri);
		return ResponseEntity.of(c);
	}
	@GetMapping("/class/fields")
	public ResponseEntity<Collection<IndexField>> classFields(
//    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	) {
		Collection<IndexField> result = classService.fields(fieldNames);  // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}

//	@GetMapping("/classes/search")
//	public ResponseEntity<List<ClassType>> searchClasses(
//			@RequestParam(name="query") String query) {
//		
//		// 
//		return ResponseEntity.ok(classes.search(query));
//	}
	@GetMapping("/class/select")
	public ResponseEntity<SearchResult<ClassType>> searchClasses(
//			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows) {
		SearchResult<ClassType> result = classService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@GetMapping("/classes")
	public ResponseEntity<SearchResult<ClassType>> getClasses(
//			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="uri", required = false) Set<String> uriList, 
			@RequestParam(name="nameSpace", required = false) String nameSpace,
			@RequestParam(name="localName", required = false) Set<String> localNames, 
			@RequestParam(required = false) String property) {
		if (property != null) {
			SearchResult<ClassType> result = classService.findByProperty(property);
			return ResponseEntity.ok(result);
		}
		if (uriList != null && !uriList.isEmpty()) {
			SearchResult<ClassType> result = classService.findByUris(uriList);
			return ResponseEntity.ok(result);

		}
		if (nameSpace != null && localNames != null && !localNames.isEmpty()) {
			SearchResult<ClassType> result = classService.findForNamespaceAndLocalNames(nameSpace, localNames);
			return ResponseEntity.ok(result);
		}
		return ResponseEntity.ok(new SearchResult<>(new ArrayList<>()));
	}

	@DeleteMapping("/class")
	public ResponseEntity<Boolean> removeClass(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String uri) {
		classService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/class")
	public ResponseEntity<Boolean> setClass(
//			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody ClassType prop) {
		classService.set(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	@GetMapping("/party/fields")
	public ResponseEntity<Collection<IndexField>> partyFields(
//    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	) {
		Collection<IndexField> result = partyService.fields(fieldNames);  // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}
	@GetMapping("/party/select")
	public ResponseEntity<SearchResult<PartyType>> searchParty(
//			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows) {
		SearchResult<PartyType> result = partyService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@GetMapping("/party")
	public ResponseEntity<PartyType> getParty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam(defaultValue = "null") String uri) {
		if (uri.equals("null")) {
			return ResponseEntity.ok(PartyTypeUtils.template());
		}
		Optional<PartyType> res = partyService.get(uri);
		return ResponseEntity.of(res);
	}

	@DeleteMapping("/party")
	public ResponseEntity<Boolean> removeParty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String uri) {
		partyService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/party")
	public ResponseEntity<Boolean> setParty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestBody PartyType party) {
		partyService.set(party);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	@GetMapping("/property/fields")
	public ResponseEntity<Collection<IndexField>> propFields(
//    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	) {
		Collection<IndexField> result = propertyService.fields(fieldNames);  // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}

	@GetMapping("/property")
	public ResponseEntity<PropertyType> getProperty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String uri) {
		Optional<PropertyType> prop = propertyService.get(uri);
		return ResponseEntity.of(prop);
	}

	@GetMapping("/properties")
	public ResponseEntity<SearchResult<PropertyType>> getProperties(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam(name = "uri", required = false) Set<String> uri,
			@RequestParam(name = "class", required = false) Set<String> classType,
			@RequestParam(name = "nameSpace", required = false) String nameSpace,
			@RequestParam(name = "localName", required = false) Set<String> localNames,
			@RequestParam(name = "idxName", required = false) Set<String> idxNames) {
		if (uri != null && !uri.isEmpty()) {
			SearchResult<PropertyType> prop = propertyService.findByUris(uri);
			return ResponseEntity.ok(prop);
		}
		if (classType != null && !classType.isEmpty()) {
			SearchResult<PropertyType> prop = propertyService.findForClasses(classType);
			return ResponseEntity.ok(prop);
		}
		if (idxNames != null) {
			SearchResult<PropertyType> prop = propertyService.findByIdxNames(idxNames);
			return ResponseEntity.ok(prop);
		}
		if (nameSpace != null && localNames != null && !localNames.isEmpty()) {
			SearchResult<PropertyType> prop = propertyService.findForNamespaceAndLocalNames(nameSpace, localNames);
			return ResponseEntity.ok(prop);

		}
		return ResponseEntity.ok(new SearchResult<>(new ArrayList<>()));
	}

	@GetMapping("/property/select")
	public ResponseEntity<SearchResult<PropertyType>> searchProperties(
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows) {
		SearchResult<PropertyType> result = propertyService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/property")
	public ResponseEntity<Boolean> removeProperty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String uri) {
		propertyService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/property")
	public ResponseEntity<Boolean> setProperty(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestBody PropertyType prop) {
		propertyService.set(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/item/select")
	public ResponseEntity<SearchResult<ItemType>> select(
//    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows) {
		SearchResult<ItemType> result = itemService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@GetMapping("/item")
	public ResponseEntity<ItemType> getItem(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam(defaultValue = "null") String uri) {
		if (uri.equals("null")) {
			return ResponseEntity.ok(ItemUtils.template());
		}
		Optional<ItemType> result = itemService.get(uri);

		return ResponseEntity.of(result);
	}

	@DeleteMapping("/item")
	public ResponseEntity<Boolean> removeItem(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestParam String uri) {
		itemService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@PostMapping("/items")
	public ResponseEntity<Boolean> setItems(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestBody List<ItemType> store) {
		boolean result = itemService.set(store);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/item/fields")
	public ResponseEntity<Collection<IndexField>> itemFields(
//    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	) {
		Collection<IndexField> result = itemService.fields(fieldNames); // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}

	@PostMapping("/item")
	public ResponseEntity<Boolean> setItem(
//			@RequestHeader(value = "Authorization") String bearerToken, 
			@RequestBody ItemType prop) {
		itemService.set(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

}
