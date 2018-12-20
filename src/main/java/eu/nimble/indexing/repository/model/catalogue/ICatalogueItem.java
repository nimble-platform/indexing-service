package eu.nimble.indexing.repository.model.catalogue;

public interface ICatalogueItem {
	String ID_FIELD = "id";
	String TYPE_FIELD ="doctype";
	String CATALOGUE_ID_FIELD = "catalogueId";
	String LANGUAGES_FIELD = "languages";
	String LABEL_FIELD = "label_*";
	String DESC_FIELD = "desc_*";
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
	String EMISSION_STANDARD_FIELD = "emissionStandard";
	String PACKAGE_TYPE_FIELD = "package_unit_*";
	String PACKAGE_AMOUNT_FILED = "package_amount_*";
	
}
