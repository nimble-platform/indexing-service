package eu.nimble.indexing.repository.model.catalogue;

/**
 * Defines the index field names for the catalogue item
 * @author dglachs
 *
 */
public interface ICatalogueItem {
	String ID_FIELD = "id";
	String TYPE_FIELD ="doctype";
	/**
	 * the item entries must have a type value assigned
	 */
	public static final String TYPE_VALUE = "item";
	
	String CATALOGUE_ID_FIELD = "catalogueId";
	String LANGUAGES_FIELD = "languages";
	String LABEL_FIELD = "itemLabel_*";
	String LABEL_FIELD_COPY = "*_txt";
	String TEXT_FIELD = "_text_";
	String DESC_FIELD = "itemDescription_*";
	String DESC_FIELD_COPY = "*_txt";
	String PRICE_FIELD = "price_*";
	String CURRENCY_FIELD = "currency_*";
	String FREE_OF_CHARGE_FIELD = "freeOfCharge";
	String CERTIFICATE_TYPE_FIELD = "certificateType";
	String APPLICABLE_COUNTRIES_FIELD = "applicableCountries";
	String ESTIMATED_DELIVERY_TIME_FIELD = "deliveryTime_value_*";
	String ESTIMATED_DELIVERY_TIME_UNIT_FIELD = "deliveryTime_unit_*";
	String ADDITIONAL_PROPERTY_FIELD = "additionalProperty";
	String MANUFACTURER_ID_FIELD = "manufacturerId";
	String SERVICE_TYPE_FIELD = "serviceType";
	String SUPPORTED_PRODUCT_NATURE_FIELD = "supportedProductNature";
	String SUPPORTED_CARGO_TYPE_FIELD = "supportedCargoType";
	// DYNAMIC
	String PACKAGE_UNIT_FIELD = "package_unit_*";
	// DYNAMIC
	String PACKAGE_AMOUNT_FILED = "package_amount_*";
	
	String PACKAGE_TYPE_FIELD = "package_type";
	String COMMODITY_CLASSIFICATION_LABEL_FIELD ="commodityClassificationLabel";
	String COMMODITY_CLASSIFICATION_URI_FIELD = "commodityClassficationUri";
	String COMMODITY_CLASSIFICATION_MIX_FIELD = "commodityClassficationMix";
	
	String TOTAL_CAPACITY_FIELD = "totalCapacity";
	String TRANSPORT_MODE = "transportMode";
	String EMISSION_TYPE_FIELD = "emissionType";
	String EMISSION_STANDARD_FIELD = "emissionStandard";
	
	String ESTIMATED_DURATION_FIELD = "estimatedDuration";
	
	// additional property attributes
	String VALUE_QUALIFIER_FIELD = "valueQualifier";
	String PROPERTY_UNIT_FIELD = "unit_*";
	String PROPERTY_LABEL_FIELD = "propLabel_*";
	String PROPERTY_STRING_VALUE_FIELD = "propValue_s_*";
	String PROPERTY_QUANTITY_VALUE_FIELD = "propValue_q_*";
	String PROPERTY_BOOLEAN_VALUE_FIELD = "propValue_b_*";
	
	

}
