package eu.nimble.indexing.service;

import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;

public interface OntologyService {
	@Deprecated
	public static final String UBL_CBC_NS = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2#";
	public static final String NIMBLE_CATALOGUE_NS = "http://www.nimble-project.org/catalogue#";
	public static final String QUANTITY_TYPE = "QuantityType";
	public static final String CODE_TYPE = "CodeType";
	public static final String UNIT_TYPE = "UnitType";
	
	public static final String QUANTITY_PROPERTY_TYPE = "QuantityProperty";
	public static final String CODE_PROPERTY_TYPE = "CodeProperty";
	
	public static final String NIMBLE_QUANTITY_URI = NIMBLE_CATALOGUE_NS + "quantity";
	public static final String NIMBLE_CODE_URI = NIMBLE_CATALOGUE_NS + "code";
	
	public static final String UBL_QUANTITY_TYPE = UBL_CBC_NS + QUANTITY_TYPE;
	public static final String UBL_CODE_TYPE = UBL_CBC_NS + CODE_TYPE;
	public static final String NIMBLE_QUANTITY_TYPE_URI = NIMBLE_CATALOGUE_NS + QUANTITY_TYPE;
	public static final String NIMBLE_UNIT_TYPE_URI = NIMBLE_CATALOGUE_NS + UNIT_TYPE;
	public static final String NIMBLE_CODE_TYPE_URI = NIMBLE_CATALOGUE_NS + CODE_TYPE;
//	
//	public static final String UNIT_CODE = "unitCode";
//	public static final String VALUE_CODE = "code";
//	public static final String VALUE_ELEMENT = "value";
	
	public static final String HAS_UNIT = "hasUnit";
	public static final String HAS_CODE = "hasCode";

	public static final String HAS_UNIT_LIST = "hasUnitList";
	public static final String HAS_CODE_LIST = "hasCodeList";
	@Deprecated
    public static final String IS_HIDDEN_ON_UI = "isHiddenOnUI";
    
    public static final String IS_VISIBLE = "isVisible";



    public void upload(String mimeType, List<String> nameSpaces, String onto);

	boolean deleteNamespace(String namespace);
	
	default Property[] unitProperties(OntModel model) {
		Property p1 = model.createProperty(NIMBLE_CATALOGUE_NS + HAS_UNIT);
		Property p2 = model.createProperty(NIMBLE_CATALOGUE_NS + HAS_UNIT_LIST);
		return new Property[] {p1, p2};
	}
	default Property[] codeProperties(OntModel model) {
		Property p1 = model.createProperty(NIMBLE_CATALOGUE_NS + HAS_CODE);
		Property p2 = model.createProperty(NIMBLE_CATALOGUE_NS + HAS_CODE_LIST);
		return new Property[] {p1, p2};
	}
	/**
	 * retrieve the uri's for a quantity type range.
	 * @return
	 */
	default String[] quantityRangeUris() {
		return new String[] {UBL_QUANTITY_TYPE, NIMBLE_QUANTITY_TYPE_URI, NIMBLE_QUANTITY_URI, quantityPropertyURI() };
	}
	/**
	 * retrieve the uri's for a code type range.
	 * @return
	 */
	default String[] codedRangeUris() {
		return new String[] {UBL_CODE_TYPE, NIMBLE_CODE_TYPE_URI, NIMBLE_CODE_URI, codePropertyURI() };
	}
	/**
	 * uri for the super-property for QuantityType 
	 * @return
	 */
	default String quantityPropertyURI() {
		return NIMBLE_CATALOGUE_NS + QUANTITY_PROPERTY_TYPE;
	}
	/**
	 * URI for the super-property of CodeType
	 * @return
	 */
	default String codePropertyURI() {
		return NIMBLE_CATALOGUE_NS + CODE_PROPERTY_TYPE;
	}
	default String isVisibleURI() {
		return NIMBLE_CATALOGUE_NS + IS_VISIBLE;
	}
	@Deprecated
	default String isHiddenURI() {
		return NIMBLE_CATALOGUE_NS + IS_HIDDEN_ON_UI;
	}
	default String codeValueURI() {
		return NIMBLE_CATALOGUE_NS + HAS_CODE;
	}

}
