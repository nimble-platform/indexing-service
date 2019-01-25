package eu.nimble.indexing.service.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.AnyCriteria;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.ClassRepository;
import eu.nimble.indexing.service.ClassService;
import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.ClassType;
import eu.nimble.service.model.solr.owl.IClassType;
import eu.nimble.service.model.solr.owl.IPropertyType;
import eu.nimble.service.model.solr.owl.PropertyType;

@Service
public class ClassServiceImpl implements ClassService {
	// injected via Autowired
	private ClassRepository classRepo;
	
	@Resource
	private SolrTemplate solrTemplate;
	
	@Override
	public ClassType getClass(String uri) {
		return classRepo.findById(uri).orElse(null);
	}
	@Override
	public void setClass(ClassType prop) {
		classRepo.save(prop);
		
	}
	@Override
	public void removeClass(String uri) {
		ClassType c = getClass(uri);
		if (c != null) {
			classRepo.delete(c);
		}
		
	}
	@Override
	public List<ClassType> getClasses(Set<String> uri) {
		return classRepo.findByUriIn(uri);
	}
	@Override
	public List<ClassType> getClassesForProperty(String forProperty) {
		return classRepo.findByProperties(forProperty);
	}
	
	@Autowired
	public void setClassRepository(ClassRepository repository) {
		this.classRepo = repository;
	}
	@Override
	public List<ClassType> getClassesForLocalNames(String nameSpace, Set<String> localNames) {
		// TODO Auto-generated method stub
		return classRepo.findByNameSpaceAndLocalNameIn(nameSpace, localNames);
	}
	@Override
	public List<ClassType> search(String solrQuery) {
		SimpleQuery q = new SimpleQuery(solrQuery);
		Page<ClassType> page = solrTemplate.queryForGroupPage(ClassType.COLLECTION, q, ClassType.class);
		return page.getContent();
	}
	@Override
	public SearchResult<ClassType> search(String search, String language, boolean labelsOnly, Pageable page) {
		String field = IClassType.TEXT_FIELD;
		SimpleQuery q = new SimpleQuery(search, page);
		if ( language !=null ) {
			field = IClassType.LANGUAGE_TXT_FIELD.replace("*", language);
			//
			if ( labelsOnly ) {
				field = IClassType.LABEL_FIELD.replace("*", language);
			}
			Criteria crit = Criteria.where(field).contains(search);
			q = new SimpleQuery(crit, page);
			
		}
		ScoredPage<ClassType> result = solrTemplate.queryForPage(IClassType.COLLECTION, q, ClassType.class);
		return new SearchResult<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
	}

}
