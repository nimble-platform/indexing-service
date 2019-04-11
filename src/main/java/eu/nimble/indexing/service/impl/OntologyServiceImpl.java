package eu.nimble.indexing.service.impl;

import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
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
import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.service.OntologyService;
import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.ClassType;
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
//	@Autowired
//	PropertyService propRepo;
	@Override
	public boolean deleteNamespace(String namespace) {
		propRepo.deleteByNameSpace(namespace);
		classRepository.deleteByNameSpace(namespace);
		return true;
	}
	@Override
	public void upload(String mimeType, String onto) {
		
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
			if ( !p.isOntLanguageTerm()) {
				PropertyType prop = processProperty(ontModel, p);
				if ( prop != null) {
					propRepo.save(prop);
					indexedProp.add(prop);
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
			if ( !c.isOntLanguageTerm()) {
				ClassType clazz = processClazz(ontModel, c, indexedProp);
				if ( clazz != null) {
					classRepository.save(clazz);
				}
			}
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
	/**
	 * Helper method to obtain all necessary information for indexing a property
	 * @param model
	 * @param prop
	 * @return
	 */
    private PropertyType processProperty(OntModel model, OntProperty prop) {

        PropertyType index = new PropertyType();
        index.setUri(prop.getURI());
        if (prop.getRange() != null) {
            Resource range = prop.getRange();
            index.setRange(range.getURI());

            if (range.getNameSpace() != null) {
                if (range.getNameSpace().equals(XSD.NS)) {
                    switch (prop.getRange().getLocalName()) {
                        case "string":
                        case "normalizedString":
                            index.setValueQualifier(ValueQualifier.STRING);
                            break;
                        case "float":
                        case "double":
                        case "decimal":
                        case "int":
                            // accordig to discussion
                            index.setValueQualifier(ValueQualifier.NUMBER);
                            break;
                        case "boolean":
                            index.setValueQualifier(ValueQualifier.BOOLEAN);
                            break;
                        default:
                            index.setValueQualifier(ValueQualifier.STRING);
                            break;
                    }
                } else if (range.getNameSpace().equals(UBL_CBC_NS)) {
                    OntClass propClass = prop.getRange().asClass();
                    if (propClass.getURI().equals(QUANTITY_CLASS_URI)) {
                        index.setValueQualifier(ValueQualifier.QUANTITY);
                        Property hasUnitProperty = model.getProperty(NIMBLE_CATALOGUE_NS, UNIT_CODE);
                        Statement hasUnitStatement = prop.getProperty(hasUnitProperty);

                        if (hasUnitStatement != null) {
                            RDFNode hasUnitNode = hasUnitStatement.getObject();
                            index.setUnitsType(hasUnitNode.toString());
                        }
                        Property hasUnitList = model.getProperty(NIMBLE_CATALOGUE_NS, HAS_UNIT_LIST);
                        Statement hasUnitListStatement = prop.getProperty(hasUnitList);
                        if (hasUnitList != null) {
                            RDFNode hasUnitListNode = hasUnitListStatement.getObject();
                            Resource unitListResource = model.getOntResource(hasUnitListNode.toString());
                            StmtIterator iter = unitListResource.listProperties();
                            List<String> unitList = new ArrayList<String>();
                            while (iter.hasNext()) {
                                Statement stmt = iter.nextStatement();
                                Property predicate = stmt.getPredicate();
                                if (predicate.getURI().equals(UBL_CBC_NS + UNIT_CODE)) {
                                    RDFNode object = stmt.getObject();
                                    unitList.add(object.asLiteral().getValue().toString());
                                }
                            }
                            index.setUnitsTypeList(unitList);
                        }

                    } else if (propClass.getURI().equals(VALUECODE_CLASS_URI)) {
                        index.setValueQualifier(ValueQualifier.TEXT);
                        Property hasValueCodeProperty = model.getProperty(NIMBLE_CATALOGUE_NS, VALUE_CODE);
                        Statement hasValueCodeStatement = prop.getProperty(hasValueCodeProperty);
                        Property hasValueCodeListProperty = model.getProperty(NIMBLE_CATALOGUE_NS, HAS_CODE_LIST);
                        Statement hasValueCodeListStatement = prop.getProperty(hasValueCodeListProperty);

                        if (hasValueCodeStatement != null) {
                            RDFNode valueCodeNode = hasValueCodeStatement.getObject();
                            Property valueCodeProperty = model.getProperty(valueCodeNode.toString());
                            StmtIterator iter = valueCodeProperty.listProperties();
                            while (iter.hasNext()) {
                                Statement stmt = iter.nextStatement();
                                Property predicate = stmt.getPredicate();
                                if (predicate.getURI().equals(NIMBLE_CATALOGUE_NS + VALUE_ELEMENT)) {
                                    RDFNode object = stmt.getObject();
                                    index.setValueCode(object.toString());
                                }
                            }
                        }

                        if (hasValueCodeListStatement != null) {
                            RDFNode valueCodeListNode = hasValueCodeListStatement.getObject();
                            Property valueCodeListResource = model.getProperty(valueCodeListNode.toString());
                            StmtIterator iter = valueCodeListResource.listProperties();
                            List<String> valueCodeList = new ArrayList<String>();
                            while (iter.hasNext()) {
                                Statement stmt = iter.nextStatement();
                                Property predicate = stmt.getPredicate();
                                if (predicate.getURI().equals(NIMBLE_CATALOGUE_NS + VALUE_CODE)) {
                                    RDFNode object = stmt.getObject();
                                    Property codeNode = model.getProperty(object.toString());
                                    StmtIterator iter2 = codeNode.listProperties();
                                    while (iter2.hasNext()) {
                                        Statement stmt2 = iter2.nextStatement();
                                        Property predicate2 = stmt2.getPredicate();
                                        RDFNode object2 = stmt2.getObject();
                                        if (predicate2.getURI().equals(NIMBLE_CATALOGUE_NS + VALUE_ELEMENT)) {
                                            valueCodeList.add(object2.toString());
                                        }

                                    }
                                }
                            }
                            index.setValueCodesList(valueCodeList);
                        }
                    }

                }

            }
        }
        index.setLocalName(prop.getLocalName());
        index.setNameSpace(prop.getNameSpace());

        // try to find labels by searching rdfs:label and skos:prefLabel
        index.setLabel(obtainMultilingualValues(prop, RDFS.label, SKOS.prefLabel));
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
