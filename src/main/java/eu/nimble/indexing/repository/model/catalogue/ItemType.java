package eu.nimble.indexing.repository.model.catalogue;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.solr.core.mapping.ChildDocument;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.nimble.indexing.repository.model.ItemUtils;
/**
 * Document class representing a single product item
 * @author dglachs
 *
 */
@SolrDocument(collection="item")
public class ItemType implements ICatalogueItem, Serializable {
	private static final long serialVersionUID = -3631731059281154372L;

	@Id
	@Indexed(name=ID_FIELD)
	private String uri;
	/**
	 * The indexed item must have a type value assigned for
	 * proper handling of nested documents
	 */
	@Indexed(name=TYPE_FIELD, defaultValue=TYPE_VALUE)
	private String type = TYPE_VALUE;
	/**
	 * The ID of the catalogue the item belongs to
	 */
	@Indexed(name=CATALOGUE_ID_FIELD)
	private String catalogueId;
	/**
	 * List of used languages for this item
	 * The list is computed by the languages of label and descriptions
	 */
	@Indexed(name=LANGUAGES_FIELD)
	private Set<String> languages;
	
	/**
	 * Dynamic map of labels (for each language). The list of languages
	 * is available with {@link #getLanguages()}
	 * 
	 */
	@Indexed(name=LABEL_FIELD
//			, copyTo= {LABEL_FIELD_COPY}
			) @Dynamic
	private Map<String,String> name;
	/**
	 * Dynamic map of descriptions (for each language). The list of languages
	 * is available with {@link #getLanguages()} 
	 */
	@Indexed(name=DESC_FIELD

//			, copyTo= {DESC_FIELD_COPY}
			) @Dynamic
	private Map<String,String> description;
	// PRICE & Currency
	@Indexed(name=CURRENCY_FIELD) @Dynamic
	private Map<String, String> currencyMap = new HashMap<>();
	@Indexed(name=PRICE_FIELD, type="pdouble") 
	@Dynamic
	private Map<String, Double> currencyValue = new HashMap<>();
	
	@Indexed(name=APPLICABLE_COUNTRIES_FIELD)
	private Set<String> applicableCountries;
	// FREE of charge indicator
	@Indexed(name=FREE_OF_CHARGE_FIELD,type="boolean")
	private Boolean freeOfCharge;
	// certification types 
	@Indexed(name=CERTIFICATE_TYPE_FIELD)
	private Set<String> certificateType;

	// delivery time(s)
	@Indexed(name=ESTIMATED_DELIVERY_TIME_UNIT_FIELD) @Dynamic
	private Map<String, String> deliveryTimeUnit = new HashMap<>();
	@Indexed(name=ESTIMATED_DELIVERY_TIME_FIELD, type="pdouble") @Dynamic
	private Map<String, Double> deliveryTime = new HashMap<>();
	/**
	 * Map holding a list of used Unit's for packaging
	 * The 
	 */
	@Indexed(name=PACKAGE_UNIT_FIELD) @Dynamic
	private Map<String, String> packageUnit = new HashMap<>();
	/**
	 * Map holding the amounts per package unit
	 */
	@Indexed(name=PACKAGE_AMOUNT_FILED, type="pdouble") @Dynamic
	private Map<String, List<Double>> packageAmounts =new HashMap<>();
	/**
	 * nested list of additional properties
	 */
	@Indexed(name=ADDITIONAL_PROPERTY_FIELD)
	@Field(child=true, value=ADDITIONAL_PROPERTY_FIELD)
	@ChildDocument
	private Collection<AdditionalProperty> additionalProperty;
	/**
	 * Id of the corresponding manufacturer
	 */
	@Indexed(name=MANUFACTURER_ID_FIELD) 
	private String manufacturerId;
	/**
	 * Read only field - used to provide the manufacturer's details
	 * in a search result
	 */
	@ReadOnlyProperty
	private PartyType manufacturer;
	// Transportation Service Details
	@Indexed(name=SERVICE_TYPE_FIELD)
	private Set<String> serviceType;
	@Indexed(name=SUPPORTED_PRODUCT_NATURE_FIELD)
	private String supportedProductNature;
	@Indexed(name=SUPPORTED_CARGO_TYPE_FIELD)
	private String supportedCargoType;
	@Indexed(name=EMISSION_STANDARD_FIELD)
	private String emissionStandard;
	
	@Indexed(name=PACKAGE_TYPE_FIELD)
	private String packageType;
	/**
	 * Possibility for joining to product class index
	 */
	@Indexed(name=COMMODITY_CLASSIFICATION_URI_FIELD)
	private List<String> commodityClassification;
	// 
	private Map<String, String> propertyMap;
	private Map<String, String> stringValue;
	private Map<String, Boolean> booleanValue;
	private Map<String, Double> doubleValue;
	
	/**
	 * GETTER for the URI
	 * @return
	 */
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	/**
	 * For proper distinction of catalogue items and nested
	 * documents, the item must hava a type field 
	 * @see #TYPE_VALUE
	 * @return
	 */
	@JsonIgnore
	public String getTypeValue() {
		return type;
	}
	public void setTypeValue(String type) {
		this.type = type;
	}
	public String getCatalogueId() {
		return catalogueId;
	}
	public void setCatalogueId(String catalogueId) {
		this.catalogueId = catalogueId;
	}
	public Set<String> getLanguages() {
		return languages;
	}
	public void setLanguages(Set<String> language) {
//		this.languages = language;
	}
	public Map<String, String> getName() {
		return name;
	}
	public void setName(Map<String, String> name) {
		if ( name !=null ) {
			for ( String key : name.keySet()) {
				addName(key, name.get(key));
			}
		}
		else {
			this.name = name;
		}
	}
	public Map<String, String> getDescription() {
		return description;
	}
	public void setDescription(Map<String, String> description) {
		if ( description !=null ) {
			for ( String key : description.keySet()) {
				addDescription(key, description.get(key));
			}
		}
		else {
			this.description = description;
		}
	}
	public Boolean getFreeOfCharge() {
		return freeOfCharge;
	}
	public void setFreeOfCharge(Boolean freeOfCharge) {
		this.freeOfCharge = freeOfCharge;
	}
	public Set<String> getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(Set<String> certificateType) {
		this.certificateType = certificateType;
	}
	public Set<String> getApplicableCountries() {
		return applicableCountries;
	}
	public void setApplicableCountries(Set<String> applicableCountries) {
		this.applicableCountries = applicableCountries;
	}

	/**
	 * Obtain the list of indexed additional properties
	 * 
	 * @return The list of additional properties
	 */
	public Collection<AdditionalProperty> getAdditionalProperty() {
		if ( this.additionalProperty == null) {
			this.additionalProperty = new HashSet<>();
		}
		return additionalProperty;
	}
	/** 
	 * Convenience method to add a AdditionalProperty to the item
	 * @param prop
	 */
	public void addAdditionalProperty(AdditionalProperty prop) {
		getAdditionalProperty().add(prop);
	}
	/**
	 * Store the list of additional properties
	 * @param additionalProperty
	 */
	public void setAdditionalProperty(Collection<AdditionalProperty> additionalProperty) {
		this.additionalProperty = additionalProperty;
	}
	public String getManufacturerId() {
		return manufacturerId;
	}
	public void setManufacturerId(String manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	public Set<String> getServiceType() {
		return serviceType;
	}
	public void setServiceType(Set<String> serviceType) {
		this.serviceType = serviceType;
	}
	public String getSupportedProductNature() {
		return supportedProductNature;
	}
	public void setSupportedProductNature(String supportedProductNature) {
		this.supportedProductNature = supportedProductNature;
	}
	public String getSupportedCargoType() {
		return supportedCargoType;
	}
	public void setSupportedCargoType(String supportedCargoType) {
		this.supportedCargoType = supportedCargoType;
	}
	public String getEmissionStandard() {
		return emissionStandard;
	}
	public void setEmissionStandard(String emissionStandard) {
		this.emissionStandard = emissionStandard;
	}
	/**
	 * Helper method adding a (language based) label to the item
	 * @param language
	 * @param label
	 */
	public void addName(@NotNull String language, @NotNull String label) {
		if ( this.name == null) {
			this.name = new HashMap<>();
		}
		this.name.put(language, label);
		// 
		addLanguage(language);
	}
	/**
	 * Helper method adding a (language based) description to the item
	 * @param language The language (en, es, de)
	 * @param desc The description in the provided language
	 */
	public void addDescription(@NotNull String language, @NotNull String desc) {
		if ( this.description == null) {
			this.description = new HashMap<>();
		}
		this.description.put(language, desc);
		// 
		addLanguage(language);
	}
	/**
	 * Helper method used to maintain the list of 
	 * used languages
	 * @param language
	 */
	private void addLanguage(String language) {
		if ( this.languages == null) {
			this.languages = new HashSet<String>();
		}
		if ( ! this.languages.contains(language)) {
			this.languages.add(language);
		}
	}
	public void addPrice(String currency, Double price) {
		this.currencyValue.put(dynamicKey(currency, this.currencyMap), price);
	}
	public Collection<String> getCurrency() {
		return this.currencyMap.values();
	}

	public void setCurrency(Collection<String> currency) {
		this.currencyMap.clear();
		for ( String c : currency) {
			dynamicKey(c, this.currencyMap);
		}
	}
	public Map<String, Double> getPrice() {
		Map<String, Double> ret = new HashMap<>();
		for ( String key : currencyMap.keySet()) {
			ret.put(currencyMap.get(key), currencyValue.get(key));
		}
		return ret;
	}
	public void setPrice(Map<String, Double> price) {
		this.currencyValue.clear();
		for ( String c : price.keySet()) {
			addPrice(c, price.get(c));
		}
	}
	@JsonIgnore
	public Map<String, String> getDeliveryTimeUnit() {
		return deliveryTimeUnit;
	}
	public Collection<String> getDeliveryTimeUnits() {
		return this.deliveryTimeUnit.values();
	}
	public void setDeliveryTimeUnits(Collection<String> units) {
		this.deliveryTimeUnit.clear();
		for ( String unit : units ) {
			// update the packageUnit
			dynamicKey(unit,  this.deliveryTimeUnit);
		}
	}
	/**
	 * Add a new delivery time to the item. 
	 * @param unit The unit such as <i>Week(s)</i>, <i>Day(s)</i>
	 * @param time The amount of the delivery time unit
	 */
	public void addDeliveryTime(String unit, Double time) {
		this.deliveryTime.put(dynamicKey(unit, this.deliveryTimeUnit), time);
	}
	/**
	 * Getter for the delivery times per unit
	 * @return
	 */
	public Map<String, Double> getDeliveryTime() {
		Map<String, Double> result = new HashMap<>();
		for ( String dynUnitKey : this.deliveryTimeUnit.keySet()) {
			result.put(deliveryTimeUnit.get(dynUnitKey), deliveryTime.get(dynUnitKey));
		}
		return result;
	}
	public void setDeliveryTime(Map<String, Double> deliveryTime) {
		this.deliveryTime.clear();
		for ( String c : deliveryTime.keySet()) {
			addDeliveryTime(c, deliveryTime.get(c));
		}
	}

	/**
	 * Internally the package units hold
	 * @return
	 */
	@JsonIgnore
	public Map<String, String> getPackageUnit() {
		return packageUnit;
	}
	public Collection<String> getPackageUnits() {
		return this.packageUnit.values();
	}
	public void setPackageUnits(Collection<String> units) {
		this.packageUnit.clear();
		for ( String unit : units ) {
			// update the packageUnit
			dynamicKey(unit,  this.packageUnit);
		}
	}
	public void addPackageAmounts(String unit, List<Double> amounts) {
		this.packageAmounts.put(dynamicKey(unit, this.packageUnit), amounts);;
	}
	/**
	 * Getter for the package amounts per unit
	 * @return
	 */
	public Map<String, List<Double>> getPackageAmounts() {
		Map<String, List<Double>> result = new HashMap<>();
		for ( String dynUnitKey : this.packageUnit.keySet()) {
			result.put(packageUnit.get(dynUnitKey), packageAmounts.get(dynUnitKey));
		}
		return result;
	}
	public void setPackageAmounts(Map<String, List<Double>> packageAmountPerUnit) {
		this.packageAmounts.clear();
		for ( String key : packageAmountPerUnit.keySet()) {
			addPackageAmounts(key, packageAmountPerUnit.get(key));
		}
	}
	
	private String dynamicKey(String keyVal, Map<String, String> keyMap) {
		String key = ItemUtils.dynamicFieldPart(keyVal);
		keyMap.put(key, keyVal);
		return key;
	}
	public PartyType getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(PartyType manufacturer) {
		this.manufacturer = manufacturer;
	}
	public List<String> getCommodityClassification() {
		return commodityClassification;
	}
	public void setCommodityClassification(List<String> commodityClassification) {
		this.commodityClassification = commodityClassification;
	}
}
