package eu.nimble.indexing.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.ClassRepository;
import eu.nimble.indexing.repository.CodedRepository;
import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.service.OntologyService;
import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.ClassType;
import eu.nimble.service.model.solr.owl.CodedType;
import eu.nimble.service.model.solr.owl.PropertyType;
import eu.nimble.service.model.solr.owl.ValueQualifier;
/**
 * Implementation for the Ontology Service
 * 
 * This service allows uploading an RDF based ontology
 * 
 * @author dglachs
 *
 */
@Service
public class OntologyServiceImpl implements OntologyService {

	private static final Logger logger = LoggerFactory.getLogger(OntologyServiceImpl.class);
	

	@Autowired
	private PropertyRepository propRepo;
	@Autowired 
	private ClassRepository classRepository;
	@Autowired
	private CodedRepository codedRepository;
//	@Autowired
//	PropertyService propRepo;
	@Override
	public boolean deleteNamespace(String namespace) {
		propRepo.deleteByNameSpace(namespace);
		classRepository.deleteByNameSpace(namespace);
		return true;
	}
	@Override
	public void upload(String mimeType, List<String> nameSpaces, String onto) {
	
		Lang l = Lang.RDFNULL;
		switch (mimeType) {
		case "application/rdf+xml":
			l = Lang.RDFXML;
			break;
		case "application/turtle":
			l = Lang.TURTLE;
			break;
		default:
		    // 
			return;
		}
		/*
		 * Create a Model with RDFS inferencing
		 */
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
		try {
			//
			StringReader reader = new StringReader(onto);
			/*
			 * Read the input string into the Ontology Model
			 */
			RDFParser.create()
				.source(reader)
				.errorHandler(ErrorHandlerFactory.errorHandlerStrict)
				.lang(l)
				.base("http://www.nimble-project.eu/onto/")
				.parse(ontModel);
			
			/*
			 * Keep a list of indexed properties, use this list for
			 * mapping with classes 
			 */
			List<PropertyType> indexedProp = new ArrayList<>();
			/*
			 * Process all ontology properties, index them and fill
			 * the list of indexedProp
			 */
			Iterator<OntProperty> properties = ontModel.listAllOntProperties();
			while ( properties.hasNext()) {
				OntProperty p = properties.next();
				// restrict import to namespace list provided
				if (nameSpaces.isEmpty() || nameSpaces.contains(p.getNameSpace())) {
					if ( !p.isOntLanguageTerm()) {
						PropertyType prop = processProperty(ontModel, p);
						if ( prop != null) {
							propRepo.save(prop);
							indexedProp.add(prop);
						}
					}
				}
			}
			/*
			 * process all ontology classes, index them and map all
			 * properties applicable to the class 
			 */
			Iterator<OntClass> classes = ontModel.listClasses();
			while ( classes.hasNext()) {
				OntClass c = classes.next();
				// restrict import to namespace list provided
				if ( nameSpaces.isEmpty() || nameSpaces.contains(c.getNameSpace())) {
					
					if ( !c.isOntLanguageTerm()) {
						ClassType clazz = processClazz(ontModel, c, indexedProp);
						if ( clazz != null) {
							classRepository.save(clazz);
						}
					}
				}
			}
		} finally {
			ontModel.close();
		}

	}
	/**
	 * Helper method extracting all information out of the ontology model 
	 * for each distinct ontology class
	 * @param model
	 * @param clazz
	 * @param availableProps
	 * @return
	 */
	private ClassType processClazz(OntModel model, OntClass clazz, List<PropertyType> availableProps) {
		// we do store only named ontology classes, omitting anonymous 
		if (!clazz.isAnon()) {
			ClassType index = new ClassType();
			index.setUri(clazz.getURI());
			index.setLocalName(clazz.getLocalName());
			index.setNameSpace(clazz.getNameSpace());
			index.setLabel(obtainMultilingualValues(clazz, RDFS.label, DC.title, SKOS.prefLabel));
			index.setDescription(obtainMultilingualValues(clazz, DC.description));
			index.setComment(obtainMultilingualValues(clazz, RDFS.comment, DC.description, SKOS.definition));

			// hiddenlabels
			index.setHiddenLabel(obtainMultilingualHiddenLabels(clazz,SKOS.hiddenLabel, SKOS.altLabel));
			// search for properties (including properties of super classes
			index.setProperties(getProperties(clazz, availableProps));
			// search for parent / super classes
			index.setAllParents(getSuperClasses(clazz));
			index.setParents(getSuperClasses(clazz, true));
			// search for child / sub classes
			index.setAllChildren(getSubClasses(clazz));
			index.setChildren(getSubClasses(clazz, true));
			return index;
		}
		return null;
	}
	/**
	 * Get a list of all property URI's which are applicable to the provided class
	 * @param clazz
	 * @param properties
	 * @return
	 */
	private Set<String> getProperties(final OntClass clazz, List<PropertyType> properties) {
		return properties.stream()
				// filtering 
				.filter(new Predicate<PropertyType>() {
					@Override
					public boolean test(PropertyType t) {
						// filter - check whether the property is assigned to the current class
						return t.getProduct().contains(clazz.getURI());
					}
				})
				// conversion from property to string
				.map(new Function<PropertyType, String>() {
		
					@Override
					public String apply(PropertyType t) {
						// map - extract the URI 
						return t.getUri();
					}
				})
				// collect the data
				.collect(Collectors.toSet());
	}
	private ValueQualifier getValueQualifier(OntProperty prop) {
		if ( isCodeTypeProperty(prop)) {
			// when coded property, value qualifier is set to TEXT
			return ValueQualifier.TEXT;
		}
		if ( isQuantityTypeProperty(prop)) {
			// when quantity property, value qualifier is set to QUANTITY
			return ValueQualifier.QUANTITY;
		}
		// try to obtain valueQualifier from range
		return fromRange(prop.getRange());
	}
	/**
	 * Determine whether the current property is a NIMBLE quantity type
	 * @param prop
	 * @return
	 */
	private boolean isQuantityTypeProperty(OntProperty prop) {
		Property p = prop.getModel().createProperty(NIMBLE_CATALOGUE_NS, QUANTITY_TYPE);
		return prop.hasURI(p.getURI()) || prop.hasSuperProperty(prop, false);
	}
	/**
	 * determine whether the provide range resource is related to nimble quantity
	 * @param resource
	 * @return
	 */
	private boolean isOfRange(OntResource resource, String ... rangeUris) {
		for (String rangeUri : rangeUris ) {
			// 
			if ( resource.hasURI(rangeUri)) {
				return true;
			}
			Resource res = resource.getModel().createResource(rangeUri);
			if ( resource.as(OntClass.class).hasSuperClass(res) ) {
				return true;
			}
		}
		return false;
	
	}

	/**
	 * Determine whether the current property is a NIMBLE code type
	 * @param prop
	 * @return
	 */
	private boolean isCodeTypeProperty(OntProperty prop) {
		Property p = prop.getModel().createProperty(NIMBLE_CATALOGUE_NS, CODE_TYPE);
		return prop.hasURI(p.getURI()) || prop.hasSuperProperty(p, false);
	}
	private ValueQualifier fromRange(OntResource range) {
		if ( range != null && !range.isAnon() ) {
			if ( isOfRange(range, codedRangeUris()) ) {
				return ValueQualifier.TEXT;
			}
			else if ( isOfRange(range, quantityRangeUris()) ) {
				return ValueQualifier.QUANTITY;
			}
			else if (range.getNameSpace().equals(XSD.NS)) {
				return fromXSDLocalName(range.getLocalName());
			}
		}
		return null;
	}
	/**
	 * retrieve {@link ValueQualifier} from {@link XSD} localNames
	 * @param localName {@link XSD} qualifiers like <i>float</i>, <i>double</i> etc.
	 * @return qualifier or {@link ValueQualifier#STRING} by default
	 */
	private ValueQualifier fromXSDLocalName(String localName) {
        switch (localName) {
        case "float":
        case "double":
        case "decimal":
        case "int":
        	return ValueQualifier.NUMBER;
        case "boolean":
        	return ValueQualifier.BOOLEAN;
        case "string":
        case "normalizedString":
        default:
        	return ValueQualifier.STRING;
        }

	}
	/**
	 * check whether a property should be marked "invisible"
	 * @param prop
	 * @return
	 */
	private boolean isVisbileProperty(OntProperty prop) {
		Property isVisibleProperty = prop.getModel().createProperty(NIMBLE_CATALOGUE_NS, IS_VISIBLE);
		RDFNode n = prop.getPropertyValue(isVisibleProperty);
		if (n != null ) {
			return n.asLiteral().getBoolean();
		}
		
		return true;
	}


	/**
	 * Helper method to obtain all necessary information for indexing a property
	 * @param model
	 * @param prop
	 * @return
	 */
    private PropertyType processProperty(OntModel model, OntProperty prop) {

        PropertyType index = new PropertyType();
        index.setUri(prop.getURI());
        //check if the property should be hidden from the UI
//        Property isHiddenProperty = model.getProperty(NIMBLE_CATALOGUE_NS, IS_HIDDEN_ON_UI);
//        Statement isHiddenStatement = prop.getProperty(isHiddenProperty);
//		if (isHiddenStatement != null) {
//			RDFNode isHiddenNode = isHiddenStatement.getObject();
//			index.setVisible(!isHiddenNode.asLiteral().getBoolean());
////			index.setHiddenOnUI( isHiddenNode.asLiteral().getBoolean());
//		}
        index.setVisible(isVisbileProperty(prop));
		// check for the value qualifier, might be null
		ValueQualifier valueQualifier = getValueQualifier(prop);
		if ( valueQualifier != null ) {
			switch ( valueQualifier) {
			case QUANTITY:
				// when quantity search for unit properties
				Set<String> units = collectAllowedValues(prop, unitProperties(model));
				// store result as units
				index.setUnits(units);
				index.setValueQualifier(ValueQualifier.QUANTITY);
				break;
			case TEXT:
			case CODE:
				// when coded values, search for code properties
				Set<String> codes = collectAllowedValues(prop, codeProperties(model));
				index.setValueCodes(codes);
				index.setValueQualifier(ValueQualifier.TEXT);
				break;
			default:
				index.setValueQualifier(valueQualifier);
			}
		}
        index.setLocalName(prop.getLocalName());
        index.setNameSpace(prop.getNameSpace());

        // try to find labels by searching rdfs:label and skos:prefLabel
        index.setLabel(obtainMultilingualValues(prop, RDFS.label, SKOS.prefLabel));
		// hiddenlabels
        index.setHiddenLabel(obtainMultilingualHiddenLabels(prop, SKOS.hiddenLabel, SKOS.altLabel));
        // try to find labels by searching rdfs:comment and skos:definition
        index.setComment(obtainMultilingualValues(prop, RDFS.comment, SKOS.definition));
        if (index.getLabel() != null) {
            for (String label : index.getLabel().values()) {
                index.addItemFieldName(ItemType.dynamicFieldPart(label));
            }
        }

        // add the local name
        index.addItemFieldName(prop.getLocalName());
        // add the uri
        index.addItemFieldName(ItemType.dynamicFieldPart(prop.getURI()));

//		index.setLabels(processPropertyLabel(prop));
        prop.listDomain();
        if (prop.getDomain() != null && prop.getDomain().isClass()) {
            Set<String> usage = getUsage(model, prop.getDomain().asClass());
            index.getProduct().addAll(usage);
        }
        //
        Resource rdfType = prop.getRDFType();
        if (rdfType != null) {
            index.setPropertyType(rdfType.getLocalName());
        }
        return index;
    }
    /**
     * Method collecting all string literals allowed for this property
     * @param resource
     * @param props
     * @return
     */
    private Set<String> collectAllowedValues(OntResource resource, Property ... props) {
    	// 
    	Set<String> codeSet = new HashSet<String>();
    	for ( Property prop : props) {
	    		
	//    	Property unitCode = resource.getModel().createProperty(NIMBLE_CATALOGUE_NS, UNIT_CODE);
	    	
	    	StmtIterator codeList = resource.listProperties(prop);
	    	while (codeList.hasNext()) {
	    		Statement stmt = codeList.next();
	    		if ( stmt.getObject().isLiteral()) {
	    			codeSet.add(stmt.getObject().asLiteral().getString());
	    		}
	    		else if ( stmt.getObject().isResource()) {
	    			// process the nimble-list item and add the returned code
	    			codeSet.addAll(processNimbleList(stmt.getObject().as(OntResource.class), props));
	    		}
	    	}
    	}
    	return codeSet;
    }
    private Set<String> processNimbleList(OntResource listItem, Property ... props ) {
    	// TODO: check for nimble list type
    	Set<String> codeSet = new HashSet<String>();
    	for ( Property prop : props) {
   	
	    	StmtIterator codeList = listItem.listProperties(prop);
	    	while (codeList.hasNext()) {
	    		Statement stmt = codeList.next();
	    		if ( stmt.getObject().isLiteral()) {
	    			codeSet.add(stmt.getObject().asLiteral().getString());
	    		}
	    		else if ( stmt.getObject().isResource()) {
	    			// process the nimble-list item and add the returned code
	    			
	    			codeSet.add(processNimbleListItem(listItem, stmt.getObject().as(OntResource.class)));
	    		}
	    	}
    	}
    	return codeSet;
    }
    private String processNimbleListItem(OntResource list, OntResource resource) {
    	
    	// 
    	
    	CodedType index = new CodedType();
    	// 
    	index.setListId(list.getURI());
    	index.setUri(resource.getURI());
    	index.setLocalName(resource.getLocalName());
    	index.setNameSpace(resource.getNameSpace());
    	// use the code (encoded as value property)
    	index.setCode(getValueProperty(resource));
    	// try to find labels by searching rdfs:label and skos:prefLabel
    	index.setLabel(obtainMultilingualValues(resource, RDFS.label, SKOS.prefLabel));
    	// try to find labels by searching rdfs:comment and skos:definition
    	index.setComment(obtainMultilingualValues(resource, RDFS.comment, SKOS.definition));
    	codedRepository.save(index);
    	return index.getCode();
    }
    /**
     * Helper method obtaining the code property from the given resource
     * @param resource
     * @return
     */
    private String getValueProperty(OntResource resource) {
    	Property codeProperty = resource.getModel().createProperty(NIMBLE_CATALOGUE_NS, VALUE_ELEMENT);
    	// 
    	Statement stmt = resource.getProperty(codeProperty);
    	if ( stmt != null) {
    		RDFNode node = stmt.getObject();
    		if (node.isResource()){
    			return node.asResource().getLocalName();
    		}
    		else {
    			// default - treat it as Literal
    			return node.asLiteral().getString();
    		}
    	}
    	codeProperty = resource.getModel().createProperty(NIMBLE_CATALOGUE_NS, VALUE_CODE);
    	stmt = resource.getProperty(codeProperty);
    	if ( stmt != null) {
    		RDFNode node = stmt.getObject();
    		if (node.isResource()){
    			return node.asResource().getLocalName();
    		}
    		else {
    			// default - treat it as Literal
    			return node.asLiteral().getString();
    		}
    	}
    	// use the local name as fallback
    	return resource.getLocalName();
    }
	/**
	 * Helper method to extract multilingual labels
	 * @param prop
	 * @param properties
	 * @return
	 */
	private Map<String, String> obtainMultilingualValues(OntResource prop, org.apache.jena.rdf.model.Property ... properties ) {
		Map<String,String> languageMap = new HashMap<>();
		for ( org.apache.jena.rdf.model.Property property : properties ) {
			NodeIterator nIter = prop.listPropertyValues(property);
			while ( nIter.hasNext()) {
				RDFNode node = nIter.next();
				if ( node.isLiteral()) {
					String lang = node.asLiteral().getLanguage();
					if (! languageMap.containsKey(lang)) {
						languageMap.put(lang, node.asLiteral().getString());
					}
				}
			}
		}
		return languageMap;
		
	}

	/**
	 * Helper method to extract multilingual hidden and alternate labels
	 * @param prop
	 * @param properties
	 * @return
	 */
	private Map<String, Collection<String>> obtainMultilingualHiddenLabels(OntResource prop, org.apache.jena.rdf.model.Property... properties) {

		Map<String, Collection<String>> languageMap = new HashMap<String, Collection<String>>();
		for (org.apache.jena.rdf.model.Property property : properties) {
			NodeIterator nIter = prop.listPropertyValues(property);
			while (nIter.hasNext()) {
				RDFNode node = nIter.next();
				if (node.isLiteral()) {
					String lang = node.asLiteral().getLanguage();
					if (languageMap.get(lang) != null) {
						Collection<String> hiddenLabelValues = languageMap.get(lang);
						hiddenLabelValues.add(node.asLiteral().getString());
						languageMap.put(lang, hiddenLabelValues);
					} else {
						Collection<String> hiddenLabelValues = new ArrayList<String>();
						hiddenLabelValues.add(node.asLiteral().getString());
						languageMap.put(lang, hiddenLabelValues);
					}
				}
			}
		}
		return languageMap;

	}
	/**
	 * Find the classes denoted by rdfs:domain
	 * @param model
	 * @param
	 * @return
	 */
	private Set<String> getUsage(OntModel model, OntClass ontClass) {
		
		Set<String> classes = new HashSet<>();
		if ( ontClass.isUnionClass()) {
			UnionClass uc = ontClass.asUnionClass();
			RDFList list = uc.getOperands();
			for ( int i = 0; i < list.size(); i++) {
				RDFNode node = list.get(i);
				OntClass cls = model.getOntClass(node.asResource().getURI());
				if (!cls.isAnon()) {
					classes.add(cls.getURI());
					classes.addAll(getSubClasses(cls));
//					classes.addAll(getSuperClasses(cls));
				}
			}
		}
		else {
			if (ontClass.isResource() && !ontClass.isAnon()) {
				classes.add(ontClass.getURI());
				classes.addAll(getSubClasses(ontClass));
			}
		}
		
		return classes;
	}
	/**
	 * Extract all superclasses of a given class
	 * @param cls
	 * @return
	 */
	private Set<String> getSuperClasses(OntClass cls) {
		return getSuperClasses(cls, false);
	}
	private Set<String> getSuperClasses(OntClass cls, boolean direct) {
		Set<String> sup = new HashSet<>();
		Iterator<OntClass> iter = cls.listSuperClasses(direct);
		while (iter.hasNext()) {
			OntClass superClass = iter.next();
			if (! superClass.isAnon()) {
//				if (!superClass.getNameSpace().equals(RDFS.uri))
				// exclude rdfs, rdf, owl
				if (!superClass.isOntLanguageTerm()) {
					sup.add(superClass.getURI());
				}
			}
		}
		return sup;
	}

	@SuppressWarnings("unused")
	private Set<String> getDomain(OntProperty prop) {
		if ( prop.getDomain() == null) {
			return null;
		}
		Set<String> domains = new HashSet<>();
		ExtendedIterator<? extends OntResource> iter = prop.listDomain();
		while ( iter.hasNext() ) {
			OntResource r = iter.next();
			if ( ! r.isAnon()) {
				domains.add(r.getURI());
			}
		}
		return domains;
		
	}
	/**
	 * Helper method to identify all child classes
	 * @param cls
	 * @return
	 */
	private Set<String> getSubClasses(OntClass cls) {
		return getSubClasses(cls, false);
	}
	private Set<String> getSubClasses(OntClass cls, boolean direct) {
		Set<String> sub = new HashSet<>();
		Iterator<OntClass> iter = cls.listSubClasses(direct);
		while (iter.hasNext()) {
			OntClass subClass = iter.next();
			if (subClass.isURIResource()) {
				sub.add(subClass.getURI());
			}
		}
		return sub;
	}
}
