package eu.nimble.indexing.repository.model.owl;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
/**
 * SOLR Document holding the properties out of 
 * any ontologies including label, range and 
 * the usage in distinct classes
 * 
 * @author dglachs
 *
 */
@SolrDocument(collection="props")
public class PropertyType extends Named {
	static final String TYPE = "property"; 
	/**
	 * The uri of the property including namespace
	 */
	@Indexed(defaultValue=TYPE, name="doctype")
	private String type = TYPE;

	@Indexed(required=false, name="range") 
	private String range;
	
	@Indexed(required=false, name="valueQualifier")
	private String valueQualifier;
	
	@Indexed(required=false, name="used_in")
	private Collection<String> product;
	
	@Indexed(required=false, name="idxField")
	private Collection<String> itemFieldNames;

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public Collection<String> getProduct() {
		if (product == null ) {
			product = new HashSet<String>(); 
		}
		return product;
	}
	public void addProduct(String className) {
		if ( this.product == null ) {
			this.product = new HashSet<>();
		}
	}
	public void setProduct(Collection<String> className) {
		this.product = className;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Collection<String> getItemFieldNames() {
		return itemFieldNames;
	}

	public void setItemFieldNames(Collection<String> idxFieldNames) {
		this.itemFieldNames = idxFieldNames;
	}

	public String getValueQualifier() {
		return valueQualifier;
	}

	public void setValueQualifier(String valueQualifier) {
		this.valueQualifier = valueQualifier;
	}


}
