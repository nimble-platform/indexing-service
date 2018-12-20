package eu.nimble.indexing.repository.model.owl;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection="class")
public class Clazz extends Named {
	private static final String TYPE = "class"; 
	
	@Indexed(defaultValue=TYPE, name="doctype")
	private String type = TYPE;
	
	@Indexed(required=false, name="properties")
	private Collection<String> properties;

	@Indexed(required=false, name="parent")
	private Collection<String> parent;
	@Indexed(required=false, name="child")
	private Collection<String> child;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Collection<String> getProperties() {
		return properties;
	}
	public void addProperty(String property) {
		if (this.properties == null ) {
			this.properties = new HashSet<>();
		}
		this.properties.add(property);
	}
	public void setProperties(Collection<String> properties) {
		this.properties = properties;
	}
	public void addParent(String superClass) {
		if (this.parent == null ) {
			this.parent = new HashSet<>();
		}
		this.parent.add(superClass);
	}
	public Collection<String> getParent() {
		return parent;
	}
	public void setParent(Collection<String> parent) {
		this.parent = parent;
	}
	public void addChild(String childClass) {
		if (this.child == null ) {
			this.child = new HashSet<>();
		}
		this.child.add(childClass);
	}
	public Collection<String> getChild() {
		return child;
	}
	public void setChild(Collection<String> child) {
		this.child = child;
	}

}
