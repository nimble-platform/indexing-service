package eu.nimble.indexing.solr.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.SimpleFilterQuery;

public class JoinHelper {
	private static final String QUOTE = "\"";

	private Set<Field> facetFields = new HashSet<>();
	private Set<FilterQuery> filterQueries = new HashSet<>();
	private Map<String, JoinInfo> joinedList = new HashMap<>();
	
	private Map<String, Set<Field>> joinedFields = new HashMap<>();
	
	
	private Map<JoinInfo, Set<FilterQuery>> filter = new HashMap<>();
	
	public void addFilter(String fromString) {
		
		int fieldDelimPos = fromString.indexOf(":");
		if ( fieldDelimPos>0 ) {
			String fieldName = fromString.substring(0,fieldDelimPos);
			int joinDelimPos = fieldName.indexOf(".");
			Criteria crit = null;
			JoinInfo joinInfo = null;
			if ( joinDelimPos > 0 ) {
				String joinName = fieldName.substring(0,joinDelimPos);
				// find the join info
				joinInfo = JoinInfo.getJoinInfo(joinName);
				
				if (joinInfo != null) {
					// get the remainder of the join
					String joinedFieldName = fieldName.substring(joinDelimPos+1);
					crit = Criteria.where(joinedFieldName).expression(encode(fromString.substring(fieldDelimPos+1)));
					// add the join filter 
					addFilter(joinInfo, crit);
				}
			}
			else {
				crit = Criteria.where(fieldName).expression(encode(fromString.substring(fieldDelimPos+1)));
				addFilter(crit);
			}
		}
	}
	/**
	 * Add a facet field, perform join verification
	 * @param fieldName
	 */
	public void addFacetField(String fieldName) {
		int joinDelimPos = fieldName.indexOf(".");
		Field field = null;
		JoinInfo joinInfo = null;
		if ( joinDelimPos > 0 ) {
			String joinName = fieldName.substring(0,joinDelimPos);
			
			// find the join info
			joinInfo = JoinInfo.getJoinInfo(joinName);
			
			if (joinInfo != null) {
				// keep the join (mappedName and info)
				addJoin(joinName, joinInfo);
				
				// keep the mapping of the used join name
				String joinedFieldName = fieldName.substring(joinDelimPos+1);
				
				field = new SimpleField(joinedFieldName);
				// add the facet field along with the joinName
				addField(joinName, field);
			}
		}
		else {
			facetFields.add(new SimpleField(fieldName));
		}
	}
	
	public Set<String> getJoins() {
		return joinedList.keySet();
	}
	public Set<FilterQuery> getFilterQueries() {
		return filterQueries;
	}
	public Set<FilterQuery> getFilterQueries(String joinName) {
		JoinInfo join = joinedList.get(joinName);
		if ( join!= null) {
			return filter.get(join);
		}
		return new HashSet<>();
	}
	
	public Set<Field> getFacetFields() {
		return facetFields;
	}
	public Set<Field> getFacetFields(String joinName) {
		return joinedFields.get(joinName);
	}
	public JoinInfo getJoinInfo(String joinName) {
		return joinedList.get(joinName);
	}
	private void addJoin(String name, JoinInfo info) {
		if ( ! joinedList.containsValue(info)) {
			// need to have the join column as facet (if not set)
			facetFields.add(info.getField());
		}
		joinedList.put(name, info);
		
	}

	private void addField(String info, Field field) {
		if (! joinedFields.containsKey(info) ) {
			joinedFields.put(info, new HashSet<>());
		}
		joinedFields.get(info).add(field);

	}
	private void addFilter(JoinInfo info, Criteria criteria) {
		if (! filter.containsKey(info) ) {
			filter.put(info, new HashSet<>());
		}
		filter.get(info).add(new SimpleFilterQuery(criteria));
		// 
		SimpleFilterQuery joinQuery = new SimpleFilterQuery(criteria);
		joinQuery.setJoin(info.getJoin());
		filterQueries.add(joinQuery);
	}
	private void addFilter(Criteria criteria) {
		SimpleFilterQuery query = new SimpleFilterQuery(criteria);

		filterQueries.add(query);

	}

	private String encode(String in) {
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