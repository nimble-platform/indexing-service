package eu.nimble.indexing.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(value=Include.NON_EMPTY)
public class IndexField {
	/**
	 * name of the field as used in the index
	 */
	final String fieldName;
	/**
	 * languages in use
	 */
	List<String> languages;
	/**
	 * labels for this field (if any)
	 */
	Map<String, String> label;
	/**
	 * labels for this field (if any)
	 */
	Map<String, String> description;
	/**
	 * The type of the field ( string, boolean, pdouble)
	 */
	String dataType;
	/**
	 * The document count for this attribute
	 */
	Integer docCount;
	/**
	 * The dynamic field name 
	 */
	String dynamicBase;
	
	
	public IndexField(String name) {
		this.fieldName = name;
	}
	public String getFieldName() {
		return fieldName;
	}
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public Map<String, String> getLabel() {
		return label;
	}
	public void setLabel(Map<String, String> label) {
		this.label = label;
	}
	public Map<String, String> getDescription() {
		return description;
	}
	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Integer getDocCount() {
		return docCount;
	}
	public void setDocCount(Integer docCount) {
		this.docCount = docCount;
	}
	public String getDynamicBase() {
		return dynamicBase;
	}
	public void setDynamicBase(String dynamicBase) {
		this.dynamicBase = dynamicBase;
	}
	
}
