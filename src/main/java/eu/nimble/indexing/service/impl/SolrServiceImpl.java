package eu.nimble.indexing.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.service.SolrService;
import eu.nimble.service.model.solr.IndexField;
import eu.nimble.service.model.solr.SearchResult;

@Service
public abstract class SolrServiceImpl<T> implements SolrService<T> {
	@Autowired
	private SolrCrudRepository<T, String> solr;
	
	@Resource
	private SolrTemplate solrTemplate;

	@Override
	public Optional<T> get(String uri) {
		Optional<T> t =  solr.findById(uri);
		if ( t.isPresent()) {
			postSelect(t.get());
		}
		return t;
		
	}

	@Override
	public void set(T item) {
		prePersist(item);
		solr.save(item);
	}

	@Override
	public boolean set(List<T> items) {
		for (T t : items) {
			set(t);
		}
		return true;
	}
	
	public abstract String getCollection();
	public abstract Class<T> getSolrClass();

	@Override
	public void remove(String uri) {
		Optional<T> c = get(uri);
		if (c.isPresent()) {
			solr.delete(c.get());
		}
	}

	@Override
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, Pageable page) {
		Criteria qCriteria = new SimpleStringCriteria(query);
		return select(qCriteria, filterQueries, facetFields, page);
	}
	public SearchResult<T> select(Criteria query, List<String> filterQueries, List<String> facetFields, Pageable page) {
		
		FacetQuery fq = new SimpleFacetQuery(query, page);
		// add filter queries 
		if ( filterQueries != null && !filterQueries.isEmpty()) {
			// 
			for (String filter : filterQueries) {
				fq.addFilterQuery(enrichFilterQuery(filter));
			}
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			FacetOptions facetOptions = new FacetOptions();
			for (String facetField : facetFields) {
				facetOptions.addFacetOnField(facetField);
			}
			// 
			facetOptions.setFacetMinCount(1);
			fq.setFacetOptions(facetOptions);
		}
		
		FacetPage<T> result = solrTemplate.queryForFacetPage(getCollection(),fq, getSolrClass());
		// retrieve the manufacturers
		enrichContent(result.getContent());
		

		return new SearchResult<>(result);
	}
	@Override
	public Collection<IndexField> fields() {
		
		LukeRequest luke = new LukeRequest();
		luke.setShowSchema(false);
		try {
			LukeResponse resp = luke.process(solrTemplate.getSolrClient(), getCollection());
			
			@SuppressWarnings("unchecked")
			NamedList<Object> fields = (NamedList<Object>) resp.getResponse().get("fields");
			Map<String,IndexField> inUse = getFields(fields);
			enrichFields(inUse);
			return inUse.values();
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	protected void enrichFields(Map<String, IndexField> inUse) {
		// subclasses may override
	}
	protected void enrichContent(List<T> content) {
		// subclasses may override
	}
	protected void prePersist(T t) {
		// subclasses may override
	}
	protected void postSelect(T t) {
		
	}
	protected SimpleFilterQuery enrichFilterQuery(String filterQuery) {
		return new SimpleFilterQuery();
	}
	@SuppressWarnings("unchecked")
	private Map<String, IndexField> getFields(NamedList<Object> fields)  {
		Map<String, IndexField> ffield = new HashMap<>();
		for (Map.Entry<String, Object> field : fields) {
			String name = field.getKey();
			IndexField f = new IndexField(name);
			for (Entry<String, Object> prop : (NamedList<Object>)field.getValue()) {
				switch(prop.getKey()) {
				case "type":
					f.setDataType(getDatatype(prop.getValue()));
					break;
				case "docs":
					f.setDocCount(Integer.valueOf(prop.getValue().toString()));
					break;
				case "dynamicBase":
					f.setDynamicBase(prop.getValue().toString());
					break;
				}
			}
			ffield.put(name, f);
		}
		
		return ffield;
	}
	private String getDatatype(Object type) {
		switch(type.toString()){
		case "pdouble":
			return "double";
		case "text_general":
			return "string";
		default:
			return type.toString();
		}
	}
	protected <I> Set<I> asSet(Iterable<I> list) {
		Set<I> set = new HashSet<>();
		if ( list!=null) {
			list.forEach(new Consumer<I>() {
	
				@Override
				public void accept(I t) {
					set.add(t);
					
				}
			});
		}
		return set;
	}
}
