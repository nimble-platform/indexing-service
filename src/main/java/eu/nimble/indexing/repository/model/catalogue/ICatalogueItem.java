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
	/**
	 * The id of the catalogue the item is contained in
	 */
	String CATALOGUE_ID_FIELD = "catalogueId";
	/**
	 * Collection of languages
	 */
	String LANGUAGES_FIELD = "languages";
	/**
	 * The language based label, e.g. <code>itemLabel_en</code> for english label
	 */
	String LABEL_FIELD = "itemLabel_*";
	/**
	 * Copy Field, language based. The language based label and description are stored in this field
	 */
	String LANGUAGE_TXT_FIELD = "*_txt";
	/**
	 * Copy Field. All labels, descriptions are stored in this field
	 */
	String TEXT_FIELD = "_text_";
	/**
	 * The language based description, e.g. <code>itemDescription_en</code> for the english description
	 */
	String DESC_FIELD = "itemDescription_*";
	/**
	 * The curreny label, the dynamic part is the encoded label string such as eUR for EUR
	 */
	String CURRENCY_FIELD = "currency_*";
	/**
	 * The currency based price field, e.g. <code>price_eur</code>, <code>price_usd</code> 
	 */
	String PRICE_FIELD = "price_*";
	/**
	 * The image field with qualifier (thumbnail, midsize)
	 */
	String IMAGE_URI_FIELD ="image_*";
	/**
	 * Free of charge indicator
	 */
	String FREE_OF_CHARGE_FIELD = "freeOfCharge";
	/**
	 * Certificate types
	 */
	String CERTIFICATE_TYPE_FIELD = "certificateType";
	/**
	 * List of applicable countries the item is available
	 */
	String APPLICABLE_COUNTRIES_FIELD = "applicableCountries";
	/**
	 * The delivery time, numeric
	 */
	String ESTIMATED_DELIVERY_TIME_FIELD = "deliveryTime_value_*";
	/**
	 * The delivery time unit (weeks, days) 
	 */
	String ESTIMATED_DELIVERY_TIME_UNIT_FIELD = "deliveryTime_unit_*";
	@Deprecated
	String ADDITIONAL_PROPERTY_FIELD = "additionalProperty";
	/**
	 * The id of the manufacturer's party type
	 */
	String MANUFACTURER_ID_FIELD = "manufacturerId";
	/**
	 * Service type, such as <b>Port to Port</b>, <b>Door to door</b>
	 */
	String SERVICE_TYPE_FIELD = "serviceType";
	/**
	 * The supported product nature (when delivering), such as <b>Euro Pallet</b>
	 */
	String SUPPORTED_PRODUCT_NATURE_FIELD = "supportedProductNature";
	/** 
	 * Supported cargo type
	 */
	String SUPPORTED_CARGO_TYPE_FIELD = "supportedCargoType";
	// DYNAMIC
	/**
	 * Dynamic! The packaging unit, e.g. the field name <code>package_unit_box</code> for the package type <b>Box</b>
	 */
	String PACKAGE_UNIT_FIELD = "package_unit_*";
	// DYNAMIC
	/**
	 * Dynamic The packaging amount per unit, e.g. the field name <code>package_amount_box</code> for the package type <b>Box</b>
	 */
	String PACKAGE_AMOUNT_FILED = "package_amount_*";
	/**
	 * The package type, such as
	 */
	@Deprecated
	String PACKAGE_TYPE_FIELD = "package_type";
	/**
	 * The label for any item classification
	 */
	String COMMODITY_CLASSIFICATION_LABEL_FIELD ="commodityClassificationLabel";
	/**
	 * The URI for any item classification
	 */
	String COMMODITY_CLASSIFICATION_URI_FIELD = "commodityClassficationUri";
	/**
	 * A combination of uri and label
	 */
	String COMMODITY_CLASSIFICATION_MIX_FIELD = "commodityClassficationMix";
	/**
	 * The total capacity
	 */
	String TOTAL_CAPACITY_FIELD = "totalCapacity";
	/**
	 * The total capacity's unit
	 */
	String Total_CAPACITY_UNIT_FIELD ="totalCapacityUnit";
	/**
	 * The transport mode
	 */
	String TRANSPORT_MODE = "transportMode";
	/**
	 * Emission type
	 */
	String EMISSION_TYPE_FIELD = "emissionType";
	/**
	 * Emission standard
	 */
	String EMISSION_STANDARD_FIELD = "emissionStandard";
	/**
	 * Estimated delivery duration
	 */
	String ESTIMATED_DURATION_FIELD = "estimatedDuration";
	
	// additional property attributes
	String VALUE_QUALIFIER_FIELD = "valueQualifier";
	String PROPERTY_UNIT_FIELD = "unit_*";
	String PROPERTY_LABEL_FIELD = "propLabel_*";
	String PROPERTY_STRING_VALUE_FIELD = "propValue_s_*";
	String PROPERTY_QUANTITY_VALUE_FIELD = "propValue_q_*";
	String PROPERTY_BOOLEAN_VALUE_FIELD = "propValue_b_*";
	/*
	 * qualifier1: totalCapacity
	 * qualifier2: boxes
	 * 
	 * totalQualifierBoxes_s: "boxes"
	 * totalQualifierBoxes_d: 12.0
	 * ---------
	 * qualifier1: package
	 * qualifier2: palletes
	 * 
	 * packagePalletes_s: "palletes"
	 * packagePalletes_d: 10.0
	 * ---------
	 * qualifier1: deliveryTime
	 * qualifier2: week(s)
	 * 
	 * deliveryTimeWeeks_s: "week(s)
	 * deliveryTimeWeeks_d: 2.0
	 */
	

}
