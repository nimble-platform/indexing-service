package eu.nimble.indexing.web.controller;

import java.util.*;

import eu.nimble.indexing.dto.SimpleItem;
import eu.nimble.indexing.dto.SimpleSearchResponse;
import eu.nimble.indexing.service.IdentityService;
import eu.nimble.indexing.utils.SearchEvent;
import eu.nimble.service.model.ubl.extension.QualityIndicatorParameter;
import eu.nimble.utility.LoggerUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.indexing.service.ClassService;
import eu.nimble.indexing.service.CodeService;
import eu.nimble.indexing.service.ItemService;
import eu.nimble.indexing.service.PartyService;
import eu.nimble.indexing.service.PropertyService;
import eu.nimble.indexing.utils.ItemUtils;
import eu.nimble.indexing.utils.PartyTypeUtils;
import eu.nimble.service.model.solr.FacetResult;
import eu.nimble.service.model.solr.IndexField;
import eu.nimble.service.model.solr.Search;
import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.ClassType;
import eu.nimble.service.model.solr.owl.CodedType;
import eu.nimble.service.model.solr.owl.PropertyType;
import eu.nimble.service.model.solr.party.PartyType;
import static eu.nimble.indexing.utils.OpenIdConnectUserDetails.Role.*;

@CrossOrigin
@RestController
@Api(value = "Index Controller",
		description = "Search API to perform Solr operations on indexed parties (organizations), items, item-properties, "
				+ "property-codes and classes (item categories)")
public class IndexController {

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private ClassService classService;

	@Autowired
	private PartyService partyService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private CodeService codeService;

	@Autowired
	private IdentityService identityService;

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@ApiOperation(value = "", notes = "Retrieve a class (category) object with a given URI", response = ClassType.class)
	@GetMapping("/class")
	public ResponseEntity<?> getClass(
			@RequestHeader(value = "Authorization") String bearerToken,
			@ApiParam(value = "uri of the class", required = true) @RequestParam String uri) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER
				,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		Optional<ClassType> c = classService.get(uri);
		return ResponseEntity.of(c);
	}

	@ApiOperation(value = "", notes = "Retrieve specific fields in a class (category)", response = IndexField.class,
			responseContainer = "List")
	@GetMapping("/class/fields")
	public ResponseEntity<?> classFields(
    		@RequestHeader(value = "Authorization") String bearerToken,
			@ApiParam(value = "field names", required = false) @RequestParam(name = "fieldName", required = false)
					Set<String> fieldNames) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

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

	@ApiOperation(value = "", notes = "Select a specific class (category)", response = ClassType.class,
			responseContainer = "List")
	@GetMapping("/class/select")
	public ResponseEntity<?> selectClass(
//			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows,
			@RequestHeader(value = "Authorization") String bearerToken
			)  throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		SearchResult<ClassType> result = classService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Get suggestions for a class (category)", response = FacetResult.class)
	@GetMapping("/class/suggest")
	public ResponseEntity<?> classSuggest(
			@RequestParam(name = "q") String query,
			@RequestParam(name = "field") String fieldName,
			@RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
			@RequestParam(name = "minCount", required = false, defaultValue = "1") int minCount,
			@RequestHeader(value = "Authorization") String bearerToken
	) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		FacetResult result = classService.suggest(query, fieldName, limit, minCount);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Search for a class (category)", response = SearchResult.class)
	@PostMapping("/class/search")
	public ResponseEntity<?> searchClass(
			@RequestBody Search search,
			@RequestHeader(value = "Authorization") String bearerToken) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		SearchResult<ClassType> result = classService.search(search);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve specific classes (category) search results ", response = SearchResult.class)
	@GetMapping("/classes")
	public ResponseEntity<?> getClasses(
			@RequestParam(name="uri", required = false) Set<String> uriList,
			@RequestParam(name="nameSpace", required = false) String nameSpace,
			@RequestParam(name="localName", required = false) Set<String> localNames,
			@RequestParam(required = false) String property,
			@RequestHeader(value = "Authorization") String bearerToken) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER
				,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

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

	@ApiOperation(value = "", notes = "Delete a class (category)", response = Boolean.class)
	@DeleteMapping("/class")
	public ResponseEntity<?> removeClass(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam String uri) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER) == false)
			return new ResponseEntity<>("User Not Allowed To Delete Class", HttpStatus.UNAUTHORIZED);

		classService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Index a class (category)", response = Boolean.class)
	@PostMapping("/class")
	public ResponseEntity<?> setClass(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody ClassType prop) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		classService.set(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Retrieve a specific value-code with a given uri", response = CodedType.class)
	@GetMapping("/code")
	public ResponseEntity<?> getCode(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam String uri) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE, PUBLISHER,NIMBLE_USER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		Optional<CodedType> c = codeService.get(uri);
		return ResponseEntity.of(c);
	}

	@ApiOperation(value = "", notes = "Retrieve specific fields in a value-code", response = IndexField.class,
			responseContainer = "List")
	@GetMapping("/code/fields")
	public ResponseEntity<?> codeFields(
    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);
		Collection<IndexField> result = codeService.fields(fieldNames);  // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}

//	@GetMapping("/classes/search")
//	public ResponseEntity<List<ClassType>> searchClasses(
//			@RequestParam(name="query") String query) {
//
//		//
//		return ResponseEntity.ok(classes.search(query));
//	}

	@ApiOperation(value = "", notes = "Select a specific value-code", response = SearchResult.class)
	@GetMapping("/code/select")
	public ResponseEntity<?> selectCode(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows) throws Exception {
		SearchResult<CodedType> result = codeService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve suggestions for value-codes", response = FacetResult.class)
	@GetMapping("/code/suggest")
	public ResponseEntity<?> codeSuggest(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q") String query,
			@RequestParam(name = "field") String fieldName,
			@RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
			@RequestParam(name = "minCount", required = false, defaultValue = "1") int minCount

			) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		FacetResult result = codeService.suggest(query, fieldName, limit, minCount);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Search for a specific value-code", response = SearchResult.class)
	@PostMapping("/code/search")
	public ResponseEntity<?> searchCode(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody Search search) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER
				,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		SearchResult<CodedType> result = codeService.search(search);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve a search result for value-codes for given parameters", response =
			SearchResult.class)
	@GetMapping("/codes")
	public ResponseEntity<?> getCodes(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="uri", required = false) Set<String> uriList,
			@RequestParam(name="listId", required = false) String listId,
			@RequestParam(name="nameSpace", required = false) String nameSpace,
			@RequestParam(name="localName", required = false) Set<String> localNames) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER,LEGAL_REPRESENTATIVE, PUBLISHER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		if (uriList != null && !uriList.isEmpty()) {
			SearchResult<CodedType> result = codeService.findByUris(uriList);
			return ResponseEntity.ok(result);
		}
		if ( listId != null && !listId.isEmpty()) {
			SearchResult<CodedType> result = codeService.findByListId(listId);
			return ResponseEntity.ok(result);
		}
		if (nameSpace != null && localNames != null && !localNames.isEmpty()) {
			SearchResult<CodedType> result = codeService.findForNamespaceAndLocalNames(nameSpace, localNames);
			return ResponseEntity.ok(result);
		}
		return ResponseEntity.ok(new SearchResult<>(new ArrayList<>()));
	}

	@ApiOperation(value = "", notes = "Delete a specific value-code", response = Boolean.class)
	@DeleteMapping("/code")
	public ResponseEntity<?> removeCode(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam String uri) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER) == false)
			return new ResponseEntity<>("User Not Allowed To Delete Class", HttpStatus.UNAUTHORIZED);

		codeService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Index a value-code", response = Boolean.class)
	@PostMapping("/code")
	public ResponseEntity<?> setCode(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody CodedType prop) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE, PUBLISHER,NIMBLE_USER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		codeService.set(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Retrieve specific fields in party index", response = IndexField.class,
			responseContainer = "List")
	@GetMapping("/party/fields")
	public ResponseEntity<?> partyFields(
    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	)  throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE, PUBLISHER,NIMBLE_USER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		Collection<IndexField> result = partyService.fields(fieldNames);  // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Select specific parties", response = SearchResult.class)
	@GetMapping("/party/select")
	public ResponseEntity<?> selectParty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE, PUBLISHER,NIMBLE_USER,
				COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		SearchResult<PartyType> result = partyService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve suggestions for specific parties", response = FacetResult.class)
	@GetMapping("/party/suggest")
	public ResponseEntity<?> partySuggest(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q") String query,
			@RequestParam(name = "field") String fieldName,
			@RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
			@RequestParam(name = "minCount", required = false, defaultValue = "1") int minCount
			) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		FacetResult result = partyService.suggest(query, fieldName, limit, minCount);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Search for specific parties", response = SearchResult.class)
	@PostMapping("/party/search")
	public ResponseEntity<?> searchParty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody Search search) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);
		//mdc logging
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("activity", SearchEvent.SEARCH_PARTY.getActivity());
		String queryString = "";
		if(search.getQuery() != null) {
			queryString = search.getQuery();
		}
		LoggerUtils.logWithMDC(logger, paramMap, LoggerUtils.LogLevel.INFO, "Searching a party with query: {}",
				queryString);
		SearchResult<PartyType> result = partyService.search(search);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve a specific party identified by the given uri", response = PartyType.class)
	@GetMapping("/party")
	public ResponseEntity<?> getParty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(defaultValue = "null") String uri)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);

		if (uri.equals("null")) {
			return ResponseEntity.ok(PartyTypeUtils.template());
		}
		Optional<PartyType> res = partyService.get(uri);
		//mdc logging
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("activity", SearchEvent.GET_PARTY.getActivity());
		LoggerUtils.logWithMDC(logger, paramMap, LoggerUtils.LogLevel.INFO, "Getting a party with uri: {}", uri);
		return ResponseEntity.of(res);
	}

	@ApiOperation(value = "", notes = "Delete a party", response = Boolean.class)
	@DeleteMapping("/party")
	public ResponseEntity<?> removeParty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam String uri) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER) == false)
			return new ResponseEntity<>("Only platform managers are allowed to delete companies",
					HttpStatus.UNAUTHORIZED);

		partyService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Index a party", response = Boolean.class)
	@PutMapping("/party")
	public ResponseEntity<?> setParty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody PartyType party) throws Exception  {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		partyService.set(party);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Reindex Party With Trust Updates", response = SearchResult.class)
	@PostMapping("/party/trust")
	public ResponseEntity<?> partyTrustUpdate(
			@RequestParam(name = "partyId") String partyId,
			@RequestBody eu.nimble.service.model.ubl.commonaggregatecomponents.PartyType partyType,
			@RequestHeader(value = "Authorization") String bearerToken) throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points", HttpStatus.UNAUTHORIZED);


		if (partyId.equals("null")) {
			return ResponseEntity.ok(PartyTypeUtils.template());
		}

		Optional<PartyType> res = partyService.get(partyId);
		PartyType indexParty = res.get();

		// get trust scores
		partyType.getQualityIndicator().forEach(qualityIndicator -> {
			if(qualityIndicator.getQualityParameter() != null && qualityIndicator.getQuantity() != null) {
				if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.COMPANY_RATING.toString())) {
									indexParty.setTrustRating(qualityIndicator.getQuantity().getValue().doubleValue());
				} else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.TRUST_SCORE.toString())) {
									indexParty.setTrustScore(qualityIndicator.getQuantity().getValue().doubleValue());
				} else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.DELIVERY_PACKAGING.toString())) {
									indexParty.setTrustDeliveryPackaging(qualityIndicator.getQuantity().getValue().doubleValue());
				} else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.FULFILLMENT_OF_TERMS.toString())) {
									indexParty.setTrustFullfillmentOfTerms(qualityIndicator.getQuantity().getValue().doubleValue());
				} else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.SELLER_COMMUNICATION.toString())) {
									indexParty.setTrustSellerCommunication(qualityIndicator.getQuantity().getValue().doubleValue());
				} else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.NUMBER_OF_TRANSACTIONS.toString())) {
									indexParty.setTrustNumberOfTransactions(qualityIndicator.getQuantity().getValue().doubleValue());
				} else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.Number_OF_EVALUATIONS.toString())) {
									indexParty.setTrustNumberOfEvaluations(qualityIndicator.getQuantity().getValue().doubleValue());
				} else if(qualityIndicator.getQualityParameter().contentEquals(QualityIndicatorParameter.TRADING_VOLUME.toString())) {
									indexParty.setTrustTradingVolume(qualityIndicator.getQuantity().getValue().doubleValue());
				}
			}
		});
		partyService.set(indexParty);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Retrieve specific fields of property index", response = IndexField.class,
			responseContainer = "List")
	@GetMapping("/property/fields")
	public ResponseEntity<?> propFields(
    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		Collection<IndexField> result = propertyService.fields(fieldNames);  // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve a property identified by a uri", response = PropertyType.class)
	@GetMapping("/property")
	public ResponseEntity<?> getProperty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam String uri)throws Exception {
		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		Optional<PropertyType> prop = propertyService.get(uri);
		return ResponseEntity.of(prop);
	}

	@ApiOperation(value = "", notes = "Retrieve a search result of properties with given parameters",
			response = SearchResult.class)
	@GetMapping("/properties")
	public ResponseEntity<?> getProperties(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "uri", required = false) Set<String> uri,
			@RequestParam(name = "class", required = false) Set<String> classType,
			@RequestParam(name = "nameSpace", required = false) String nameSpace,
			@RequestParam(name = "localName", required = false) Set<String> localNames,
			@RequestParam(name = "idxName", required = false) Set<String> idxNames) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

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

	@ApiOperation(value = "", notes = "Retrieve suggestions for properties", response = FacetResult.class)
	@GetMapping("/property/suggest")
	public ResponseEntity<?> propertySuggest(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q") String query,
			@RequestParam(name = "field") String fieldName,
			@RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
			@RequestParam(name = "minCount", required = false, defaultValue = "1") int minCount
			)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);
		FacetResult result = propertyService.suggest(query, fieldName, limit, minCount);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Select specific properties", response = SearchResult.class)
	@GetMapping("/property/select")
	public ResponseEntity<?> selectProperties(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		SearchResult<PropertyType> result = propertyService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Search for a property", response = SearchResult.class)
	@PostMapping("/property/search")
	public ResponseEntity<?> searchProperties(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody Search search)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		SearchResult<PropertyType> result = propertyService.search(search);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Delete a property", response = Boolean.class)
	@DeleteMapping("/property")
	public ResponseEntity<?> removeProperty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam String uri)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,NIMBLE_USER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		propertyService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Index a property", response = Boolean.class)
	@PostMapping("/property")
	public ResponseEntity<?> setProperty(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody PropertyType prop)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		propertyService.set(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Retrieve suggestions for items", response = FacetResult.class)
	@GetMapping("/item/suggest")
	public ResponseEntity<?> itemSuggest(
			@RequestParam(name = "q") String query,
			@RequestParam(name = "field") String fieldName,
			@RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
			@RequestParam(name = "minCount", required = false, defaultValue = "1") int minCount,
			@RequestHeader(value = "Authorization") String bearerToken
			) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		FacetResult result = itemService.suggest(query, fieldName, limit, minCount);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Select items for a given search criteria", response = SearchResult.class)
	@GetMapping("/item/select")
	public ResponseEntity<?> selectItem(
    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
			@RequestParam(name = "fq", required = false) List<String> filterQuery,
			@RequestParam(name = "facet.field", required = false) List<String> facetFields,
			@RequestParam(name = "facet.limit", required = false, defaultValue = "15") int facetLimit,
			@RequestParam(name = "facet.mincount", required = false, defaultValue = "1") int facetMinCount,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows) throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		SearchResult<ItemType> result = itemService.select(query, filterQuery, facetFields, facetLimit, facetMinCount,
				new SolrPageRequest(start, rows));
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Search for templates", response = Search.class)
	@GetMapping("/search/template")
	public ResponseEntity<?> searchTemplate(
			@RequestHeader(value = "Authorization") String bearerToken
			)throws Exception{

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		Search result = new Search("query")
				.filter("fieldName:filter")
				.facetField("fieldName");


		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Search items for a given search criteria", response = SearchResult.class)
	@PostMapping("/item/search")
	public ResponseEntity<?> searchItem(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody Search search)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		SearchResult<ItemType> result = itemService.search(search);
		//mdc logging
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("activity", SearchEvent.SEARCH_ITEM.getActivity());
		String queryString = "";
		if(search.getQuery() != null) {
			queryString = search.getQuery();
		}
		LoggerUtils.logWithMDC(logger, paramMap, LoggerUtils.LogLevel.INFO, "Searching an item with query: {}",
				queryString);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve a specific item for the given uri", response = ItemType.class)
	@GetMapping("/item")
	public ResponseEntity<?> getItem(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(defaultValue = "null") String uri) throws Exception  {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		if (uri.equals("null")){
			return ResponseEntity.ok(ItemUtils.template());
		}
		Optional<ItemType> result = itemService.get(uri);
		//mdc logging
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("activity", SearchEvent.GET_ITEM.getActivity());
		LoggerUtils.logWithMDC(logger, paramMap, LoggerUtils.LogLevel.INFO, "Getting an item with uri: {}", uri);
		return ResponseEntity.of(result);
	}

	@ApiOperation(value = "", notes = "Detele an item", response = Boolean.class)
	@DeleteMapping("/item")
	public ResponseEntity<?> removeItem(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam String uri)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		itemService.remove(uri);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Index items", response = Boolean.class)
	@PostMapping("/items")
	public ResponseEntity<?> setItems(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody List<ItemType> store)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		boolean result = itemService.set(store);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Retrieve specific fields in items index", response = IndexField.class,
			responseContainer = "List")
	@GetMapping("/item/fields")
	public ResponseEntity<?> itemFields(
    		@RequestHeader(value = "Authorization") String bearerToken,
			@RequestParam(name="fieldName", required=false) Set<String> fieldNames
	)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		Collection<IndexField> result = itemService.fields(fieldNames); // (query, new SolrPageRequest(0, 10));
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "", notes = "Index an item", response = Boolean.class)
	@PostMapping("/item")
	public ResponseEntity<?> setItem(
			@RequestHeader(value = "Authorization") String bearerToken,
			@RequestBody ItemType prop)throws Exception {

		if (identityService.hasAnyRole(bearerToken, PLATFORM_MANAGER,LEGAL_REPRESENTATIVE,NIMBLE_USER,
				PUBLISHER,COMPANY_ADMIN,EFACTORYUSER) == false)
			return new ResponseEntity<>("User Not Allowed To Access The Indexing End Points",
					HttpStatus.UNAUTHORIZED);

		itemService.set(prop);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@ApiOperation(value = "", notes = "Provides flat search with minimal data", response = SearchResult.class)
	@GetMapping("/item/simpleSearch")
	public ResponseEntity<?> simpleSearch(@RequestParam(name = "q", required = false, defaultValue = "*:*") String query,
                                          @RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
                                          @RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows)throws Exception {

        Search search = new Search();
	    search.setQuery(query);
	    search.setRows(rows);
	    search.setStart(start);

		SearchResult<ItemType> result = itemService.search(search);
		SimpleSearchResponse simpleSearchResponse = new SimpleSearchResponse();
		List<SimpleItem> simpleItemList = new ArrayList<>();



		for (ItemType i : result.getResult()) {
			SimpleItem simpleItem = new SimpleItem();

            if (null != i.getPrice()) {
                simpleItem.setPrice(String.valueOf(i.getPrice().get("EUR")));
            }
            if(null != i.getLabel()){
                simpleItem.setItemName(i.getLabel().get("en"));
            }
            if (null != i.getManufactuerItemId()) {
                simpleItem.setItemID(i.getManufactuerItemId());
            }
            if (null != i.getCatalogueId()) {
                simpleItem.setCatalogueID(i.getCatalogueId());
            }
			if (null != i.getManufacturer()) {
				simpleItem.setManufacturerName(i.getManufacturer().getLegalName());
			}
            if (null != i.getImgageUri() && (i.getImgageUri().toArray()).length != 0) {
                simpleItem.setImageURI((i.getImgageUri().toArray())[0].toString());
            }

			String classificationQuery = "";
			for (String c : i.getClassificationUri()) {
				if (classificationQuery.isEmpty()) {
                    classificationQuery = classificationQuery + "id:\"" + c + "\"";
				}else {
                    classificationQuery = classificationQuery + " OR id:\"" + c + "\"";
				}
			}

			search = new Search();
			search.setQuery(classificationQuery);

			SearchResult<ClassType> classResults = classService.search(search);
			List<String> categoryNames = new ArrayList<>();
			for (ClassType c: classResults.getResult()) {
				categoryNames.add(c.getLabel().get("en"));
			}
			simpleItem.setCategory(categoryNames);

            simpleItemList.add(simpleItem);
		}
		simpleSearchResponse.setItemList(simpleItemList);
		return ResponseEntity.ok(simpleSearchResponse);
	}

}
