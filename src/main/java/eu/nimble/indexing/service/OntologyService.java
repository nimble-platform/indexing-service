package eu.nimble.indexing.service;

public interface OntologyService {
	public static final String UBL_CBC_NS = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2#";
	public static final String NIMBLE_CATALOGUE_NS = "http://www.nimble-project.org/catalogue#";
	public static final  String QUANTITY_TYPE = "QuantityType";
	public static final  String VALUE_CODE_TYPE = "CodeType";

	public static final String QUANTITY_CLASS_URI = UBL_CBC_NS + QUANTITY_TYPE;
	public static final String VALUECODE_CLASS_URI = UBL_CBC_NS + VALUE_CODE_TYPE;
	public static final String UNIT_CODE = "unitCode";
	public static final String VALUE_CODE = "code";
	public static final String VALUE_ELEMENT = "value";

	public static final String HAS_UNIT_LIST = "hasUnitList";
	public static final String HAS_CODE_LIST = "hasCodeList";


	public void upload(String mimeType, String onto);

	boolean deleteNamespace(String namespace);

}
