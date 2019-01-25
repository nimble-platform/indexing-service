package eu.nimble.indexing.service.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.model.SearchResult;
import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.service.PropertyService;
import eu.nimble.service.model.solr.owl.IPropertyType;
import eu.nimble.service.model.solr.owl.PropertyType;

@Service
public class PropertyServiceImpl implements PropertyService {
	// injected via Autowired
	private PropertyRepository propRepo;
	
	@Resource
	private SolrTemplate solrTemplate;
	
	@PostConstruct
	public void init() {
		
	}
	@Override
	public PropertyType getProperty(String uri) {
		// 
		List<PropertyType> props = propRepo.findByUri(uri);//.orElse(null);
		if (props.size() > 0) {
			return props.get(0);
		}
		return null;
	}

	@Override
	public void setProperty(PropertyType prop) {
		propRepo.save(prop);
	}

	@Override
	public void removeProperty(String uri) {
		PropertyType prop  = getProperty(uri);
		if ( prop != null) {
			propRepo.delete(prop);
		}
	}
	
	@Override
	public SearchResult<PropertyType> search(String search, String language, boolean labelsOnly, Pageable page) {
		String field = IPropertyType.TEXT_FIELD;
		if ( language !=null ) {
			field = IPropertyType.LANGUAGE_TXT_FIELD.replace("*", language);
			//
			if ( labelsOnly ) {
				field = IPropertyType.LABEL_FIELD.replace("*", language);
			}
			
		}
		Criteria crit = Criteria.where(field).contains(search);
		SimpleQuery query = new SimpleQuery(crit, page);
		ScoredPage<PropertyType> result = solrTemplate.queryForPage(IPropertyType.COLLECTION, query, PropertyType.class);
		return new SearchResult<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
	}

	

	@Autowired
	public void setPropertyRepository(PropertyRepository repository) {
		this.propRepo = repository;
	}

	@Override
	public List<PropertyType> getProperties(String forClass) {
		return propRepo.findByProduct(forClass);
	}
	@Override
	public List<PropertyType> getPropertiesByName(String nameSpace, Set<String> names) {
		return propRepo.findByNameSpaceAndLocalNameIn(nameSpace, names);
	}
	@Override
	public List<PropertyType> getPropertiesByUri(Set<String> uri) {
		return propRepo.findByUriIn(uri);
	}
	@Override
	public List<PropertyType> getPropertiesByIndexName(Set<String> names) {
		return propRepo.findByItemFieldNamesIn(names);
	}
	@Override
	public void removeByNamespace(String namespace) {
		propRepo.deleteByNameSpace(namespace);
	}
	@Override
	public SearchResult<PropertyType> search(String search, String language, Pageable page) {
		return search(search,language, false, page);
	}
	
}
