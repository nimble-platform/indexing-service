package eu.nimble.indexing.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

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
import org.springframework.data.solr.core.query.Join;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleField;
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
	private static final String QUOTE = "\"";
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
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, int facetLimit, Pageable page) {
		// expand main query to a wild card search when it is only a single word
		if (query.indexOf(":") == -1 && query.indexOf("*") == -1 && query.indexOf(" ") == -1)   {
			query = String.format("*%s*", query);
		}
		Criteria qCriteria = new SimpleStringCriteria(query);
		return select(qCriteria, filterQueries, facetFields, facetLimit, page);
	}
	@Override
	public SearchResult<T> select(Criteria query, List<String> filterQueries, List<String> facetFields, int facetLimit, Pageable page) {
		
		FacetQuery fq = new SimpleFacetQuery(query, page);
		// add filter queries 
		if ( filterQueries != null && !filterQueries.isEmpty()) {
			// 
			for (String filter : filterQueries) {
				fq.addFilterQuery(parseFilterQuery(filter));
			}
		}
		for (String f : getSelectFieldList()) {
			fq.addProjectionOnField(new SimpleField(f));
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			FacetOptions facetOptions = new FacetOptions();
			for (String facetField : facetFields) {
				facetOptions.addFacetOnField(facetField);
			}
			// 
			facetOptions.setFacetMinCount(1);
			facetOptions.setFacetLimit(facetLimit);
			fq.setFacetOptions(facetOptions);
		}
		
		FacetPage<T> result = solrTemplate.queryForFacetPage(getCollection(),fq, getSolrClass());
		// enrich content - to be overloaded by subclasses
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
			//
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
		// subclasses may override
	}
	protected Join getJoin(String joinName) {
		// subclasses may override
		return null;
	}
	protected String[] getSelectFieldList() {
		return new String[] {};
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
		case "plong":
			return "long";
		case "pint":
			return "int";
		case "pdouble":
			return "double";
		case "text_general":
			return "string";
		default:
			// string & boolean
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

	private SimpleFilterQuery parseFilterQuery(String fromString) {
		int fieldDelimPos = fromString.indexOf(":");
		String fieldName = fromString.substring(0,fieldDelimPos);
		int joinDelimPos = fieldName.indexOf(".");
		Criteria crit = null;
		Join join = null;
		if ( joinDelimPos > 0 ) {
			String joinName = fieldName.substring(0,joinDelimPos);
			String joinedFieldName = fieldName.substring(joinDelimPos+1);
			join = getJoin(joinName);
			crit = Criteria.where(joinedFieldName).expression(encode(fromString.substring(fieldDelimPos+1)));			
		}
		else {
			crit = Criteria.where(fieldName).expression(encode(fromString.substring(fieldDelimPos+1)));
		}
		SimpleFilterQuery q =  new SimpleFilterQuery(crit);
		if ( join!=null) {
			q.setJoin(join);
		}
		return q;
	}
	public static String encode(String in) {
		return in;
//		try {
//			if ( URLEncoder.encode(in,"utf8").length() == in.length()) {
//				return in;
//			}
//			// special characters present, need to wrap in quotes
//			else {
//				if (  ( in.startsWith(QUOTE)) && in.endsWith(QUOTE) ) {
//					// quotes present
//					return in;
//				}
//				// no quotes present 
//				return String.format("%s%s%s", QUOTE, in, QUOTE);
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			return in;
//		}
	}

}
