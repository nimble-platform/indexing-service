package eu.nimble.indexing.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.CodedRepository;
import eu.nimble.indexing.service.CodeService;
import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.CodedType;

@Service
public class CodeServiceImpl extends SolrServiceImpl<CodedType> implements CodeService {
	@Autowired
	private CodedRepository classRepo;
	@Override
	public String getCollection() {
		return CodedType.COLLECTION;
	}


	@Override
	public SearchResult<CodedType> findByUris(Set<String> uriSet) {
		List<CodedType> result = classRepo.findByUriIn(uriSet);
		return new SearchResult<CodedType>(result);
	}

	@Override
	public SearchResult<CodedType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames) {
		List<CodedType> result = classRepo.findByNameSpaceAndLocalNameIn(nameSpace, localNames);
		return new SearchResult<CodedType>(result);
	}


	@Override
	public SearchResult<CodedType> findByListIdAndCode(String listId, String code) {
		List<CodedType> result = classRepo.findByListIdAndCode(listId, code);
		return new SearchResult<CodedType>(result);
	}


	@Override
	public Class<CodedType> getSolrClass() {
		return CodedType.class;
	}


	@Override
	public SearchResult<CodedType> findByListId(String listId) {
		List<CodedType> result = classRepo.findByListId(listId);
		return new SearchResult<CodedType>(result);
	}
}
