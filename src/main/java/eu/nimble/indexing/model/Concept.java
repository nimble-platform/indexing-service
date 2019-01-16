package eu.nimble.indexing.model;

import java.util.Collection;
import java.util.Map;

import eu.nimble.indexing.repository.model.owl.Named;

public class Concept {

	private String uri;
	private Collection<String> languages;
	private Map<String, String> label;
	private Map<String, String> description;
	
	public static Concept buildFrom(Named named) {
		Concept c = new Concept();
		c.setUri(named.getUri());
		c.setLanguages(named.getLanguages());
		c.setLabel(named.getLabel());
		c.setDescription(named.getComment());
		return c;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Collection<String> getLanguages() {
		return languages;
	}
	public void setLanguages(Collection<String> languages) {
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
}
