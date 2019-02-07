package eu.nimble.indexing.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.Join;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.ClassRepository;
import eu.nimble.indexing.service.ClassService;
import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.ClassType;

@Service
public class ClassServiceImpl extends SolrServiceImpl<ClassType> implements ClassService {
	@Autowired
	private ClassRepository classRepo;
	@Override
	public String getCollection() {
		return ClassType.COLLECTION;
	}

	@Override
	public Class<ClassType> getSolrClass() {
		return ClassType.class;
	}

	@Override
	public SearchResult<ClassType> findByProperty(String property) {
		List<ClassType> result = classRepo.findByProperties(property);
		return new SearchResult<ClassType>(result);
	}

	@Override
	public SearchResult<ClassType> findByUris(Set<String> uriSet) {
		List<ClassType> result = classRepo.findByUriIn(uriSet);
		return new SearchResult<ClassType>(result);
	}

	@Override
	public SearchResult<ClassType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames) {
		List<ClassType> result = classRepo.findByNameSpaceAndLocalNameIn(nameSpace, localNames);
		return new SearchResult<ClassType>(result);
	}

	@Override
	protected Join getJoin(String joinName) {
		return ClassType.JOIN_TO.getJoin(joinName);
	}


}
