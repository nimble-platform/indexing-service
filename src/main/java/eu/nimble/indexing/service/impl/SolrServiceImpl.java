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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.service.SolrService;
import eu.nimble.indexing.solr.query.JoinHelper;
import eu.nimble.indexing.solr.query.JoinInfo;
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
	public SearchResult<T> select(String query, List<String> filterQueries, List<String> facetFields, int facetLimit, int facetMinCount, Pageable page) {
		// expand main query to a wild card search when it is only a single word
		if (query.indexOf(":") == -1 && query.indexOf("*") == -1 && query.indexOf(" ") == -1)   {
			query = String.format("*%s*", query);
		}
		Criteria qCriteria = new SimpleStringCriteria(query);
		// 
		JoinHelper joinHelper = new JoinHelper();
		
		if ( filterQueries != null && !filterQueries.isEmpty()) {
			// 
			for (String filter : filterQueries) {
				joinHelper.addFilter(filter);
			}
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			for (String fieldName : facetFields ) {
				// facet field could be a joined field
				joinHelper.addFacetField(fieldName);
//				Field facetField = parseFacetField(fieldName, joinedFacets);
//				if (facetField != null) 
//					fieldList.add(facetField);
////				fieldList.add(new SimpleField(fieldName));
			}
		}
		SearchResult<T> result = select(qCriteria, joinHelper.getFilterQueries(), joinHelper.getFacetFields(), facetLimit, facetMinCount, page);
		
		for (String join : joinHelper.getJoins()) {
			// process join facets
			if (!joinHelper.getFacetFields(join).isEmpty()) {
				// process a query for the joined facet fields
				joinFacets(result, join, joinHelper.getJoinInfo(join), joinHelper.getFilterQueries(join), joinHelper.getFacetFields(join), 100, 1, page);
			}
			
		}
		return result;
	}
	private SearchResult<T> select(
			Criteria query, 
			Set<FilterQuery> filterQueries, 
			Set<Field> facetFields, 
			int facetLimit, 
			int facetMinCount, 
			Pageable page) {
		
		FacetQuery fq = new SimpleFacetQuery(query, page);
		// add filter queries

		if ( filterQueries != null && !filterQueries.isEmpty()) {
			// 
			for (FilterQuery filter : filterQueries) {
				fq.addFilterQuery(filter);
			}
		}
		for (Field f : getSelectFieldList()) {
			fq.addProjectionOnField(f);
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			FacetOptions facetOptions = new FacetOptions();
			for (Field facetField : facetFields) {
				facetOptions.addFacetOnField(facetField);
			}
			// 
			facetOptions.setFacetMinCount(facetMinCount);
			facetOptions.setFacetLimit(facetLimit);
			fq.setFacetOptions(facetOptions);
		}
		
		FacetPage<T> result = solrTemplate.queryForFacetPage(getCollection(),fq, getSolrClass());
		
		
		// enrich content - to be overloaded by subclasses
		enrichContent(result.getContent());
		

		return new SearchResult<>(result);
	}
	private SearchResult<T> joinFacets(
			SearchResult<T> toExtend,
			String joinName,
			JoinInfo join,
			Set<FilterQuery> filterQueries, 
			Set<Field> facetFields,
			int facetLimit,
			int facetMinCount,
			Pageable page) {
		
//		if ( toExtend.getFacets().containsKey(join.getField().getName()) ) {
//			// TODO: check whether we need to restrict the facet filter to the list of 
//			//       manufacturers (taken from facet)
//			
//		}
		FacetQuery fq = new SimpleFacetQuery(new SimpleStringCriteria("*:*"), page);
		// we are interested in facets only
		fq.setRows(0);
		// add filter queries
		if ( filterQueries != null && !filterQueries.isEmpty()) {
			// 
			for (FilterQuery filter : filterQueries) {
				fq.addFilterQuery(filter);
			}
		}
		if ( facetFields != null && !facetFields.isEmpty()) {
			FacetOptions facetOptions = new FacetOptions();
			for (Field facetField : facetFields) {
				facetOptions.addFacetOnField(facetField);
			}
			// 
			
			facetOptions.setFacetMinCount(facetMinCount);
			facetOptions.setFacetLimit(facetLimit);
			fq.setFacetOptions(facetOptions);
		}
		FacetPage<?> result = solrTemplate.queryForFacetPage(join.getJoinedCollection(),fq, join.getJoinedType());
		for (Field field :  result.getFacetFields()) {
			Page<FacetFieldEntry> facetResultPage = result.getFacetResultPage(field);
			//
			for (FacetFieldEntry entry : facetResultPage.getContent() ) {
				// add the entry value with the mapped name
				String mappedField = String.format("%s.%s",  joinName, entry.getField().getName());
				toExtend.addFacet(mappedField, entry.getValue(), entry.getValueCount());
			}
		}

		return toExtend;
	}

	@Override
	public Collection<IndexField> fields() {
		return fields(null);
	}
	public Collection<IndexField> fields(Set<String> fieldNames) {
		
		LukeRequest luke = new LukeRequest();
		luke.setShowSchema(false);
		try {
			LukeResponse resp = luke.process(solrTemplate.getSolrClient(), getCollection());
			
			@SuppressWarnings("unchecked")
			NamedList<Object> fields = (NamedList<Object>) resp.getResponse().get("fields");
			Map<String,IndexField> inUse = getFields(fields, fieldNames);
			// enrich required fields
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
	protected Collection<Field> getSelectFieldList() {
		return new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, IndexField> getFields(NamedList<Object> fields, Set<String> requested)  {
		Map<String, IndexField> ffield = new HashMap<>();
		for (Map.Entry<String, Object> field : fields) {
			String name = field.getKey();
			if ( (requested == null || requested.isEmpty()) 
				// when requested list present and not empty
				|| isRequestedField(requested, name)) {
				
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
		}
		
		return ffield;
	}
	private boolean isRequestedField(Set<String> requested, final String current) {
		Optional<String> found = requested.stream().filter(new Predicate<String>() {

			@Override
			public boolean test(String t) {
				if ( t.startsWith("*")) {
					return current.endsWith(t.substring(1));
				}
				else if ( t.endsWith("*")) {
					return current.startsWith(t.substring(0,  t.length()-1));
				}
				else {
					return t.equals(current);
				}
			}}).findFirst();
		return found.isPresent(); 
	}
	/**
	 * Translate the solr datatype
	 * @param type
	 * @return
	 */
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
	/**
	 * Express the list as Set - removing (possible) duplicates
	 * @param list
	 * @return
	 */
	protected <I> Set<I> asSet(List<I> list) {
		if ( list!= null && !list.isEmpty()) {
			return list.stream().collect(Collectors.toSet());
		}
		return new HashSet<>();
//		if ( list!=null) {
//			list.forEach(new Consumer<I>() {
//	
//				@Override
//				public void accept(I t) {
//					set.add(t);
//					
//				}
//			});
//		}
//		return set;
	}

	public static String encode(String in) {
		// check for a colon - ensure URI's are quoted
		if (in.contains(":")) {
			if (  ( in.startsWith(QUOTE)) && in.endsWith(QUOTE) ) {
				// quotes present
				return in;
			}
			// no quotes present 
			return String.format("%s%s%s", QUOTE, in, QUOTE);
		}
		return in;
	}

}
