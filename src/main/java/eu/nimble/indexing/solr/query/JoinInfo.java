package eu.nimble.indexing.solr.query;

import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.Join;
import org.springframework.data.solr.core.query.SimpleField;

import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.ClassType;
import eu.nimble.service.model.solr.owl.IClassType;
import eu.nimble.service.model.solr.owl.IPropertyType;
import eu.nimble.service.model.solr.owl.PropertyType;
import eu.nimble.service.model.solr.party.IParty;
import eu.nimble.service.model.solr.party.PartyType;
/**
 * 
 * List of "allowed" join indicators in the <code>fq</code> and <code>facet.field</code> parameters 
 * for the distinct ../select methods.
 * <p>
 * A join must be indicated by the join name, e.g. manufacturer, party, product, class followed by a dot "." and
 * the respective filter or field expression. joined facets are computed and added to the main query. Possible 
 * joins are
 * 
 * <ul>
 * <li>item --> party via {@link ItemType#getManufacturerId()}: use join names <i>manufacturer</i> or <i>party</i> 
 * <li>item --> classification via {@link ItemType#getClassificationUri()}: use join names <i>productType</i> or <i>classfication</i> 
 * <li>class --> property via {@link ClassType#getProperties()}: use join names <i>property</i> or <i>prop</i> 
 * <li>property --> class via {@link PropertyType#getProduct()}: use join names <i>product</i> or <i>productType</i> or <i>class</i> 
 * </ul>
 * </p>
 * Example:
 * <pre>
 * fq=manufacturer.trustScore:[5 TO 10]
 * fq=property.en_txt:*Rack*
 * </pre>
 * <p>
 * Faceting on joined collections is possible the adding the indicated field to the list of <code>facet.field</code> parameters.
 * <pre>
 * facet.field=party.trustScore
 * facet.field=property.nameSpace
 * </pre>
 * this will issue a facet query on the joined collection and add the result to the main query. Join filters will be 
 * applied to the facet query on the joined collection. 
 * </p>
 * @author dglachs
 *
 */
public enum JoinInfo {
	// join from items to party type (manufacturer)
	manufacturer(
			ItemType.COLLECTION, 
			ItemType.MANUFACTURER_ID_FIELD, 
			IParty.COLLECTION, 
			IParty.ID_FIELD, 
			PartyType.class, 
			"manufacturer", "party"),
	// join from items to classes (furniture ontology, eClass)
	classification(
			ItemType.COLLECTION, 
			ItemType.COMMODITY_CLASSIFICATION_URI_FIELD, 
			IClassType.COLLECTION, 
			IClassType.ID_FIELD, 
			ClassType.class, 
			"productType", "classification", "class" ),
	// join from classification to items
	item(
			IClassType.COLLECTION,
			IClassType.ID_FIELD,
			ItemType.COLLECTION,
			ItemType.COMMODITY_CLASSIFICATION_URI_FIELD,
			ItemType.class,
			"item" 
			),
	// join from class to props 
	property(
			IClassType.COLLECTION, 
			IClassType.PROPERTIES_FIELD, 
			IPropertyType.COLLECTION, 
			IPropertyType.ID_FIELD, 
			PropertyType.class, 
			"property", "prop", "props"),
	// join from props to class	
	product(
			IPropertyType.COLLECTION, 
			IPropertyType.USED_WITH_FIELD, 
			IClassType.COLLECTION, 
			IClassType.ID_FIELD, 
			ClassType.class, 
			"product", "productType", "class"),

	;
	/** 
	 * the main (outgoing) collection
	 */
	String collection;
	/**
	 * The field in the 
	 */
	String joinedField;
	String field;
	String joinedCollection;
	String[] names;
	Class<?> classType;
	
	JoinInfo(String mainCollection,  String mainField, String joinedCollection, String joinedField, Class<?> classType, String ... names) {
		this.collection = mainCollection;
		this.joinedField = joinedField;
		this.field = mainField;
		this.joinedCollection = joinedCollection;
		this.names = names;
		this.classType = classType;
	}
	public Field getField() {
		return new SimpleField(field);
	}
	public static JoinInfo getJoinInfo(String collection, String mappedName) {
		for ( JoinInfo j : values()) {
			if ( j.collection.equals(collection)) {
				if ( j.names != null ) {
					for (String s : j.names) {
						if ( s.equalsIgnoreCase(mappedName)) {
							return j;
						}
					}
				}
			}
		}
		// not found - try the enum name
		try {
			// check for ItemType JOINS
			JoinInfo info = JoinInfo.valueOf(mappedName.toLowerCase());
			// 
			return info;
		} catch (Exception e) {
			// invalid join
			return null;
		}
	}
	@Deprecated
	public static JoinInfo getJoinInfo(String mappedName) {
		for ( JoinInfo j : values()) {
			if ( j.names != null ) {
				for (String s : j.names) {
					if ( s.equalsIgnoreCase(mappedName)) {
						return j;
					}
				}
			}
		}
		// not found - try the enum name
		try {
			// check for ItemType JOINS
			JoinInfo info = JoinInfo.valueOf(mappedName.toLowerCase());
			// 
			return info;
		} catch (Exception e) {
			// invalid join
			return null;
		}
	}
		
	public Join getJoin() {
		return new Join(new SimpleField(joinedField), new SimpleField(field), joinedCollection);
	}
	public String getJoinedCollection() {
		return joinedCollection;
	}
	public Class<?> getJoinedType() {
		return classType;
	}

}