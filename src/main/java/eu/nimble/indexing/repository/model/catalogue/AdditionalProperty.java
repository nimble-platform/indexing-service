package eu.nimble.indexing.repository.model.catalogue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.jena.ext.com.google.common.base.CaseFormat;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(value=Include.NON_EMPTY)
@SolrDocument(collection="item")
public class AdditionalProperty {
	public static final String TYPE = "additionalProperty";
	@Id
	@Indexed
	private String id;
	
	@Field("doctype")
	@Indexed(name="doctype", defaultValue=TYPE)
	private String type = TYPE;
	// STRING, QUANTITY, BOOLEAN
	@Indexed
	private String valueQualifier;
	
	@Indexed 
	private Collection<String> languages;
	
	@Indexed(name="unit_*") @Dynamic
	private Map<String, String> unitMap;
	
	@Indexed(name="label_*") @Dynamic
	private Map<String, String> name;
	
	@Indexed(name="*_string") @Dynamic
	private Map<String, String> value;
	
	@Indexed(name="*_quantity", type="pdouble") @Dynamic
	private Map<String, Double> doubleUnitValue;
	
	@Indexed(name="*_boolean", type="boolean") @Dynamic
	private Map<String, Boolean> booleanUnitValue;
	


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	@JsonIgnore
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getValueQualifier() {
		return valueQualifier;
	}


	public void setValueQualifier(String valueQualifier) {
		this.valueQualifier = valueQualifier;
	}


	public Collection<String> getLanguages() {
		return languages;
	}


	public void setLanguages(Collection<String> languages) {
//		this.languages = languages;
	}

	@JsonIgnore
	public Map<String, String> getUnitMap() {
		return unitMap;
	}

	public void setUnits(Collection<String> units) {
		this.unitMap = new HashMap<>();
		for (String unit : units) {
			this.addUnit(unit);
		}
	}

	public Map<String, String> getName() {
		return name;
	}


	public void setName(Map<String, String> name) {
		if ( name !=null ) {
			for ( String key : name.keySet()) {
				addName(key, name.get(key));
			}
		}
		else {
			this.name = name;
		}
	}
	public void addName(String language, String label) {
		if ( this.name == null) {
			this.name = new HashMap<>();
		}
		this.name.put(language, label);
		// 
		addLanguage(language);
	}
	public void addValue(String unit, Boolean value) {
		if ( this.booleanUnitValue == null) {
			this.booleanUnitValue = new HashMap<>();
		}
		String key = addUnit(unit);
		this.booleanUnitValue.put(key, value);
	}
	public void addValue(String unit, Double value) {
		if ( this.doubleUnitValue == null) {
			this.doubleUnitValue = new HashMap<>();
		}
		String key = addUnit(unit);
		this.doubleUnitValue.put(key, value);
	}
	public void addValue(String unit, String value) {
		if ( this.value == null) {
			this.value = new HashMap<>();
		}
		String key = addUnit(unit);
		this.value.put(key, value);
		
		// 
	}
	private void addLanguage(String language) {
		if ( this.languages == null) {
			this.languages = new HashSet<String>();
		}
		this.languages.add(language);
	}
	public Collection<String> getUnits() {
		if ( this.unitMap != null ) {
			return this.unitMap.values();
		}
		return null;
	}
	/**
	 * produce a SOLR save name from the provided string
	 * @param unit
	 * @return
	 */
	private String addUnit(String unit) {
		if ( this.unitMap == null) {
			this.unitMap = new HashMap<>();
		}
		if (! StringUtils.hasText(unit)) {
			// when no unit code specified - use "undefined";
			return "undefined";
		}
		String input = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, unit);
		input = input.replaceAll("[^a-zA-Z0-9_ ]", "");
		input = input.trim().replaceAll(" ", "_").toUpperCase();
		input = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, input);
		this.unitMap.put(input, unit);
		return input;
		
	}


	public Map<String, String> getValue() {
		if (value != null ) {
			// units must be not null
			Map<String, String> ret = new HashMap<>();
			for (String key : value.keySet()) {
				ret.put(this.unitMap.get(key), this.value.get(key));
			}
			return ret;
		}
		return value;
	}
	
	public Map<String, Double> getQuantity() {
		if (doubleUnitValue != null ) {
			// units must be not null
			Map<String, Double> ret = new HashMap<>();
			for (String key : doubleUnitValue.keySet()) {
				ret.put(this.unitMap.get(key), this.doubleUnitValue.get(key));
			}
			return ret;
		}
		return doubleUnitValue;
	}
	public void setQuantity(Map<String, Double> qMap) {
		if ( qMap !=null ) {
			for ( String key : qMap.keySet()) {
				addValue(key, qMap.get(key));
			}
		}
	}
	public Map<String, Boolean> getBoolean() {
		if (booleanUnitValue != null ) {
			// units must be not null
			Map<String, Boolean> ret = new HashMap<>();
			for (String key : booleanUnitValue.keySet()) {
				ret.put(this.unitMap.get(key), this.booleanUnitValue.get(key));
			}
			return ret;
		}
		return booleanUnitValue;
	}
	public void setBoolean(Map<String, Boolean> bMap) {
		if ( bMap !=null ) {
			for ( String key : bMap.keySet()) {
				addValue(key, bMap.get(key));
			}
		}
	}
	public void setValue(Map<String, String> value) {
		if ( value !=null ) {
			for ( String key : value.keySet()) {
				addValue(key, value.get(key));
			}
		}
		else {
			this.value = value;
		}
	}

	public boolean equals(Object other) {
		if (!(other instanceof AdditionalProperty)) {
			return false;
		}
		AdditionalProperty otherProp =(AdditionalProperty) other;
		if ( id != null && otherProp.id != null) {
			return id.equals(otherProp.id);
		}
		else {
			return false;
		}
	}
}
