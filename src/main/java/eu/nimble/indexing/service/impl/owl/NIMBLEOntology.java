package eu.nimble.indexing.service.impl.owl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.ErrorHandlerFactory;


public class NIMBLEOntology {
	public static final String NS = "http://www.nimble-project.org/catalogue#";
	public static final String QUANTITY_TYPE = "QuantityType";
	public static final String CODE_TYPE = "CodeType";
	public static final String UNIT_TYPE = "UnitType";
	public static final String LIST_TYPE = "ListType";
	public static final String UNIT_LIST = "UnitList";
	public static final String CODE_LIST = "CodeList";
	// property localName's
	public static final String HAS_CODE  = "hasCode";
	public static final String HAS_UNIT  = "hasUnit";
	public static final String HAS_CODE_LIST = "hasCodeList";
	public static final String HAS_UNIT_LIST = "hasUnitList";
	public static final String CODE      = "code";
	private static final String UNIT_CODE = "unitCode";
	public static final String IS_VISIBLE = "isVisible";
	public static final String ID        = "id";
	
	public static final String QUANTITY_PROPERTY_TYPE = "QuantityProperty";
	public static final String CODE_PROPERTY_TYPE = "CodeProperty";

	
	private static final String ONT_FILE = "/NIMBLEOntology.owl";
	private static NIMBLEOntology instance;
	private final OntModel nimbleModel;
//	private final Set<OntProperty> CODE_TYPE_PROPS = new HashSet<OntProperty>();
//	private final Set<OntProperty> UNIT_TYPE_PROPS = new HashSet<OntProperty>();
	private final Set<OntProperty> UNIT_LIST_PROPS = new HashSet<OntProperty>();
	private final Set<OntProperty> CODE_LIST_PROPS = new HashSet<OntProperty>();
	private final Set<OntProperty> CODE_PROPERTY_PROPS = new HashSet<OntProperty>();
	private final Set<OntProperty> QUANTITY_PROPERTY_PROPS = new HashSet<OntProperty>();
//	private final OntProperty codeTypeProperty;
//	private final OntProperty unitTypeProperty;
//	private final OntClass codeType;
//	private final OntClass unitType;
//	private final OntClass codeListType;
//	private final OntClass unitListType;

	private NIMBLEOntology() {
		nimbleModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
//		codeTypeProperty = nimbleModel.createOntProperty(NS + CODE_PROPERTY_TYPE);
//		unitTypeProperty = nimbleModel.createOntProperty(NS + QUANTITY_PROPERTY_TYPE);
//		codeType = nimbleModel.createClass(NS + CODE_TYPE);
//		unitType = nimbleModel.createClass(NS + UNIT_TYPE);
//		unitListType = nimbleModel.createClass(NS + UNIT_LIST);
//		codeListType = nimbleModel.createClass(NS + CODE_LIST);
//		
		try {
			load(nimbleModel, ONT_FILE, Lang.RDFXML);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		// code type properties
//		CODE_TYPE_PROPS.add(getOntProperty(NS+HAS_CODE));
//		CODE_TYPE_PROPS.add(getOntProperty(NS+HAS_CODE_LIST));
//		CODE_TYPE_PROPS.add(getOntProperty(NS+CODE));
//		// 
//		// unittype properties (hasCode, code, hasUnit
//		UNIT_TYPE_PROPS.add(getOntProperty(NS+HAS_CODE));
//		UNIT_TYPE_PROPS.add(getOntProperty(NS+CODE));
//		UNIT_TYPE_PROPS.add(getOntProperty(NS+HAS_UNIT));
		// code list properties (hasCode, code)
		CODE_LIST_PROPS.add(getOntProperty(NS+HAS_CODE));
		CODE_LIST_PROPS.add(getOntProperty(NS+CODE));
		// UNIT list properties (hasCode, code, hasUnit)
		UNIT_LIST_PROPS.add(getOntProperty(NS+HAS_CODE));
		UNIT_LIST_PROPS.add(getOntProperty(NS+CODE));
		UNIT_LIST_PROPS.add(getOntProperty(NS+HAS_UNIT));
		// 
		// quantity property
		QUANTITY_PROPERTY_PROPS.add(getOntProperty(NS+HAS_CODE));
		QUANTITY_PROPERTY_PROPS.add(getOntProperty(NS+HAS_CODE_LIST));
		QUANTITY_PROPERTY_PROPS.add(getOntProperty(NS+HAS_UNIT));
		QUANTITY_PROPERTY_PROPS.add(getOntProperty(NS+HAS_UNIT_LIST));
		QUANTITY_PROPERTY_PROPS.add(getOntProperty(NS+CODE));
		QUANTITY_PROPERTY_PROPS.add(getOntProperty(NS+UNIT_CODE));

		//
		// code property elements
		CODE_PROPERTY_PROPS.add(getOntProperty(NS+HAS_CODE));
		CODE_PROPERTY_PROPS.add(getOntProperty(NS+HAS_CODE_LIST));
		CODE_PROPERTY_PROPS.add(getOntProperty(NS+CODE));
	}
	
	private static NIMBLEOntology getInstance() {
		if ( instance == null) {
			instance = new NIMBLEOntology();
		}
		return instance;
	}
	
	private void load(OntModel model, String name, Lang lang) throws IOException {
		InputStream inStream = getClass().getResourceAsStream(name);
		try {
			RDFParser.create()
			.source(inStream)
			.errorHandler(ErrorHandlerFactory.errorHandlerStrict)
			.lang(lang)
			.base("http://www.nimble-project.eu/onto/")
			.parse(model);
		} finally {
			inStream.close();
		}
	}
	/**
	 * Check whether the provided {@link OntProperty} is a
	 * subproperty of nimble:{@value #QUANTITY_PROPERTY_TYPE}
	 * @param resource
	 * @return
	 */
	public static boolean isQuantityProperty(OntProperty resource) {
		return getInstance().checkUnitTypeProperty(resource);
	}
	private boolean checkUnitTypeProperty(OntProperty resource) {
		OntProperty unitTypeProperty = getOntProperty(NS + QUANTITY_PROPERTY_TYPE);
		return resource.hasURI(unitTypeProperty.getURI()) || resource.hasSuperProperty(unitTypeProperty, false);
	}
	/**
	 * Check whether the provided {@link OntProperty} is a
	 * subproperty of nimble:{@value #CODE_PROPERTY_TYPE}
	 * @param resource
	 * @return
	 */
	public static boolean isCodeProperty(OntProperty resource) {
		return getInstance().checkCodeTypeProperty(resource);
	}
	private boolean checkCodeTypeProperty(OntProperty resource) {
		OntProperty codeTypeProperty = getOntProperty(NS + CODE_PROPERTY_TYPE);
		return resource.hasURI(codeTypeProperty.getURI()) || resource.hasSuperProperty(codeTypeProperty, false);
	}
	/**
	 * Check whether the provided {@link OntResource} is a
	 * subproperty of nimble:{@value #CODE_LIST}
	 * @param resource
	 * @return
	 */
	public static boolean isCodeList(OntResource resource) {
		return getInstance().checkCodeList(resource);
	}
	private boolean checkCodeList(OntResource resource) {
		OntClass codeList = getOntClass(NS + CODE_LIST);
		return resource.hasURI(codeList.getURI()) || resource.hasRDFType(codeList);
	}
	/**
	 * Check whether the provided resource is of rdf:type nimble:{@value #LIST_TYPE}
	 * @param resource
	 * @return
	 */
	public static boolean isListType(OntResource resource) {
		if ( isUnitList(resource) || isCodeList(resource)) {
			return true;
		}
		return false;
	}
	/**
	 * Check whether the provided {@link OntResource} has the
	 * rdf:type of nimble:{@value #UNIT_LIST} assigned 
	 * 
	 * @param resource
	 * @return
	 */
	public static boolean isUnitList(OntResource resource) {
		return getInstance().checkUnitList(resource);
	}
	private boolean checkUnitList(OntResource resource) {
		OntClass codeList = getOntClass(NS + UNIT_LIST);
		return resource.hasURI(codeList.getURI()) || resource.hasRDFType(codeList, false);
	}
	/**
	 * Check whether the provided {@link OntResource} has the
	 * rdf:type of nimble:{@value #CODE_TYPE} assigned 
	 * @param resource
	 * @return
	 */
	public static boolean isCodeType(OntResource resource) {
		return getInstance().checkCodeType(resource);
	}
	private boolean checkCodeType(OntResource resource) {
		OntClass codeList = getOntClass(NS + CODE_TYPE);
		return resource.hasURI(codeList.getURI()) || resource.hasRDFType(codeList, false);
	}
	private OntClass getOntClass(String uri) {
		OntClass cls = nimbleModel.getOntClass(uri);
		if ( cls == null) {
			cls = nimbleModel.createClass(uri);
		}
		return cls;
	}
	private OntProperty getOntProperty(String uri) {
		OntProperty prop = nimbleModel.getOntProperty(uri);
		if ( prop == null ) {
			prop = nimbleModel.createOntProperty(uri);
		}
		return prop;
	}
	/**
	 * Check whether the provided {@link OntResource} has the
	 * rdf:type of nimble:{@value #UNIT_TYPE} assigned 
	 * @param resource
	 * @return
	 */
	public static boolean isUnitType(OntResource resource) {
		return getInstance().checkUnitType(resource);
	}
	private boolean checkUnitType(OntResource resource) {
		OntClass codeList = getOntClass(NS + UNIT_TYPE);
		return resource.hasURI(codeList.getURI()) || resource.hasRDFType(codeList, false);
	}
	/**
	 * Return an iterator covering all relevant properties for {@link OntResource}s 
	 * of type 
	 * <ul>
	 * <li>nimble: {@value #CODE_PROPERTY_TYPE} - element defining a list of codes
	 * <li>nimble: {@value #QUANTITY_PROPERTY_TYPE} - element defining a list of units
	 * <li>nimble: {@value #CODE_LIST} - individual element defining a list of codes
	 * <li>nimble: {@value #UNIT_LIST} - individual defining a list of units
	 * </ul>
	 * @param resource
	 * @return
	 */
	public static Iterator<Statement> listNimbleStatements(OntResource resource) {
		return getInstance().obtainNimbleStatements(resource).iterator();
	}
	private Set<Statement> obtainNimbleStatements(OntResource resource) {
		
		Set<Statement> statements = new HashSet<Statement>();
		for (OntProperty prop : getNimbleProperties(resource) ) {
			StmtIterator iter = resource.listProperties(prop);
			while (iter.hasNext()) {
				Statement statement = iter.next();
				statements.add(statement);
			}
		}
		return statements;
	}
	private Set<OntProperty> getNimbleProperties(OntResource forResource) {
		if ( checkCodeList(forResource)) {
			return getProperties(getOntClass(NS+CODE_LIST));
		}
		else if ( checkUnitList(forResource)){
			return getProperties(getOntClass(NS+UNIT_LIST));		
		}
		else if ( checkCodeType(forResource)){
			return getProperties(getOntClass(NS+CODE_TYPE));		
		}
		else if ( checkUnitType(forResource)){
			return getProperties(getOntClass(NS+UNIT_TYPE));		
		}
		else if ( checkUnitTypeProperty(forResource.asProperty())){
			return getProperties(getOntClass(NS+QUANTITY_PROPERTY_TYPE));		
		}
		else if ( checkCodeTypeProperty(forResource.asProperty())){
			return getProperties(getOntClass(NS+CODE_PROPERTY_TYPE));		
		}
		else {
			return new HashSet<OntProperty>();
		}
	}
	private Set<OntProperty> getProperties(OntResource resource) {
		switch (resource.getLocalName()) {
		case CODE_LIST:
			return CODE_LIST_PROPS;
		case UNIT_LIST:
			return UNIT_LIST_PROPS;
//		case UNIT_TYPE:
//			return UNIT_TYPE_PROPS;
//		case CODE_TYPE:
//			return CODE_TYPE_PROPS;
		case CODE_PROPERTY_TYPE:
			return CODE_PROPERTY_PROPS;
		case QUANTITY_PROPERTY_TYPE:
			return QUANTITY_PROPERTY_PROPS;
		default:
			return new HashSet<OntProperty>();
		}
	}
	
	private Set<OntProperty> getProperties(String resource) {
		OntResource classType = nimbleModel.createOntResource(resource);
		return getProperties(classType);
	}
	/**
	 * Retrieve the nimble:{@value #HAS_CODE} from the provided resource, when not found 
	 * return the default value
	 * @param resource
	 * @param def The default
	 * @return 
	 */
	public static String hasCode(OntResource resource, String def) {
		RDFNode node = resource.getPropertyValue(getInstance().getOntProperty(NS+HAS_CODE));
		if (node != null) {
			return node.asLiteral().getString();
		}
		// keep the fall back to nimble:code
		node = resource.getPropertyValue(getInstance().getOntProperty(NS+CODE));
		if (node != null) {
			return node.asLiteral().getString();
		}
		return def;
	}
	/**
	 * Retrieve the nimble:{@value #IS_VISIBLE} from the provided resource, when not found 
	 * return the default value
	 * @param resource
	 * @param def The default
	 * @return 
	 */
	public static boolean isVisible(OntProperty resource, boolean def) {
		RDFNode node = resource.getPropertyValue(getInstance().getOntProperty(NS+IS_VISIBLE));
		if (node != null) {
			return node.asLiteral().getBoolean();
		}
		return def;
	}
	/**
	 * Retrieve the nimble:{@value #ID} from the provided resource, when not found 
	 * return the default value
	 * @param resource
	 * @param def The default
	 * @return 
	 */
	public static String listId(OntResource resource, String def) {
		RDFNode node = resource.getPropertyValue(getInstance().getOntProperty(NS+ID));
		if (node != null) {
			return node.asLiteral().getString();
		}
		return def;
	}

	public static void main(String[] args) {
		Set<OntProperty> aList = getInstance().getProperties(NS+QUANTITY_PROPERTY_TYPE);
		Set<OntProperty> aList2 = getInstance().getProperties(NS+CODE_PROPERTY_TYPE);
		Set<OntProperty> aList3 = getInstance().getProperties(NS+CODE_TYPE);
		Set<OntProperty> aList4 = getInstance().getProperties(NS+UNIT_TYPE);
		
		
		
	}
}
