package eu.nimble.indexing.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.Field;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.service.PropertyService;
import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.IPropertyType;
import eu.nimble.service.model.solr.owl.PropertyType;

@Service
public class PropertyServiceImpl extends SolrServiceImpl<PropertyType> implements PropertyService
{
	@Autowired
	private PropertyRepository propRepo;

	@Override
	public String getCollection() {
		return PropertyType.COLLECTION;
	}

	@Override
	public Class<PropertyType> getSolrClass() {
		return PropertyType.class;
	}

	@Override
	public SearchResult<PropertyType> findByUris(Set<String> uriSet) {
		List<PropertyType> result = propRepo.findByUriIn(uriSet);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	public SearchResult<PropertyType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames) {
		List<PropertyType> result = propRepo.findByNameSpaceAndLocalNameIn(nameSpace, localNames);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	public SearchResult<PropertyType> findForClass(String classType) {
		List<PropertyType> result = propRepo.findByProduct(classType);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	public SearchResult<PropertyType> findByIdxNames(Set<String> idxNames) {
		List<PropertyType> result = propRepo.findByItemFieldNamesIn(idxNames);
		return new SearchResult<PropertyType>(result);
	}

	@Override
	protected String[] getSelectFieldList() {
		// 
		return IPropertyType.defaultFieldList();
	}

}
