package eu.nimble.indexing.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.ClassRepository;
import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.repository.model.owl.Clazz;
import eu.nimble.indexing.repository.model.owl.Property;
import eu.nimble.indexing.service.OntologyService;
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
	@Autowired
	private PropertyRepository propRepo;
	@Autowired 
	private ClassRepository clazzRepo;

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
		List<Property> indexedProp = new ArrayList<>();
		/*
		 * Process all ontology properties, index them and fill
		 * the list of indexedProp
		 */
		Iterator<OntProperty> properties = ontModel.listAllOntProperties();
		while ( properties.hasNext()) {
			OntProperty p = properties.next();
			Property prop = processProperty(ontModel, p);
			if ( prop != null) {
				propRepo.save(prop);
				indexedProp.add(prop);
			}
		}
		/*
		 * process all ontology classes, index them and map all
		 * properties applicable to the class 
		 */
		Iterator<OntClass> classes = ontModel.listClasses();
		while ( classes.hasNext()) {
			OntClass c = classes.next();
			Clazz clazz = processClazz(ontModel, c, indexedProp);
			if ( clazz != null) {
				clazzRepo.save(clazz);
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
	private Clazz processClazz(OntModel model, OntClass clazz, List<Property> availableProps) {
		// we do store only named ontology classes, omitting anonymous 
		if (!clazz.isAnon()) {
			Clazz index = new Clazz();
			index.setUri(clazz.getURI());
			index.setLocalName(clazz.getLocalName());
			index.setNameSpace(clazz.getNameSpace());
			index.setLabel(obtainMultilingualValues(clazz, RDFS.label, SKOS.prefLabel));
			index.setComment(obtainMultilingualValues(clazz, RDFS.comment, SKOS.definition));
			// search for properties (including properties of super classes
			index.setProperties(getProperties(clazz, availableProps));
			// search for parent / super classes
			index.setParent(getSuperClasses(clazz));
			// search for child / sub classes
			index.setChild(getSubClasses(clazz));
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
	private Set<String> getProperties(final OntClass clazz, List<Property> properties) {
		return properties.stream()
				// filtering 
				.filter(new Predicate<Property>() {
					@Override
					public boolean test(Property t) {
						// filter - check whether the property is assigned to the current class
						return t.getProduct().contains(clazz.getURI());
					}
				})
				// conversion from property to string
				.map(new Function<Property, String>() {
		
					@Override
					public String apply(Property t) {
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
	private Property processProperty(OntModel model, OntProperty prop) {
		Property index = new Property();
		index.setUri(prop.getURI());
		if ( prop.getRange()!= null) {
			index.setRange(prop.getRange().getURI());
		}
		index.setLocalName(prop.getLocalName());
		index.setNameSpace(prop.getNameSpace());
		// try to find labels by searching rdfs:label and skos:prefLabel
		index.setLabel(obtainMultilingualValues(prop, RDFS.label, SKOS.prefLabel));
		// try to find labels by searching rdfs:comment and skos:definition
		index.setComment(obtainMultilingualValues(prop, RDFS.comment, SKOS.definition));
		
//		index.setLabels(processPropertyLabel(prop));
		prop.listDomain();
		if ( prop.getDomain() != null && prop.getDomain().isClass()) {
			Set<String> usage = getUsage(model,prop.getDomain().asClass());
			index.getProduct().addAll(usage);
		}
		// 
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
	 * @param prop
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
		Set<String> sup = new HashSet<>();
		Iterator<OntClass> iter = cls.listSuperClasses();
		while (iter.hasNext()) {
			OntClass superClass = iter.next();
			if (! superClass.isAnon()) {
				sup.add(superClass.getURI());
			}
		}
		return sup;
	}
//	private Set<String> getProperties(OntClass clazz, boolean includeSuper) {
//		Set<String> props = getProperties(clazz);
//		if ( includeSuper) {
//			props.addAll(getSuperProperties(clazz));
//		}
//		return props;
//	}
//	private Set<String> getSuperProperties(OntClass clazz) {
//		Set<String> props = new HashSet<>();
//		ExtendedIterator<OntClass> iter = clazz.listSuperClasses();
//		while (iter.hasNext()) {
//			OntClass sup = iter.next();
//			if (! sup.isAnon()) {
//				props.addAll(getProperties(sup));
//			}
//		}
//		return props;
//	}
//	private Set<String> getProperties(OntClass clazz) {
//		Set<String> props = new HashSet<>();
//		ExtendedIterator<OntProperty> iter = clazz.listDeclaredProperties();
//		while (iter.hasNext()) {
//			OntProperty s = iter.next();
//			if ( !s.isAnon()) {
//				props.add(s.getURI());
//			}
//		}
//		return props;
//		
//	}
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
		Set<String> sub = new HashSet<>();
		Iterator<OntClass> iter = cls.listSubClasses();
		while (iter.hasNext()) {
			OntClass subClass = iter.next();
			if (subClass.isURIResource()) {
				sub.add(subClass.getURI());
			}
		}
		return sub;
	}
}
