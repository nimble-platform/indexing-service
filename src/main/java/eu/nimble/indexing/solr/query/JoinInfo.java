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

public enum JoinInfo {
//	party(IParty.ID_FIELD, ItemType.MANUFACTURER_ID_FIELD, IParty.COLLECTION),
	// join to party type (manufacturer)
	manufacturer(IParty.ID_FIELD, ItemType.MANUFACTURER_ID_FIELD, IParty.COLLECTION, PartyType.class, "manufacturer", "party"),
	// join to classes (furniture ontology, eClass)
	classification(IClassType.ID_FIELD, ItemType.COMMODITY_CLASSIFICATION_URI_FIELD, IClassType.COLLECTION, ClassType.class, "productType", "classification"),
	property(IPropertyType.ID_FIELD, IClassType.PROPERTIES_FIELD, IPropertyType.COLLECTION, PropertyType.class, "property", "prop"),
	product(IClassType.ID_FIELD, IPropertyType.USED_WITH_FIELD, IClassType.COLLECTION, ClassType.class, "product", "productType", "class"),

	;
	
	String from;
	String to;
	String fromIndex;
	String[] names;
	Class<?> classType;
	
	JoinInfo(String from, String to, String fromIndex, Class<?> classType, String ... names) {
		this.from = from;
		this.to = to;
		this.fromIndex = fromIndex;
		this.names = names;
		this.classType = classType;
	}
	public Field getField() {
		return new SimpleField(to);
	}
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
	public static Join getJoin(String name) {
		for ( JoinInfo j : values()) {
			if ( j.names != null ) {
				for (String s : j.names) {
					if ( s.equalsIgnoreCase(name)) {
						return j.getJoin();
					}
				}
			}
		}
		// not found - try the enum name
		try {
			// check for ItemType JOINS
			JoinInfo join = JoinInfo.valueOf(name.toLowerCase());
			// 
			return join.getJoin();
		} catch (Exception e) {
			// invalid join
			return null;
		}
	}		
	public Join getJoin() {
		return new Join(new SimpleField(from), new SimpleField(to), fromIndex);
	}
	public String getJoinedCollection() {
		return fromIndex;
	}
	public Class<?> getJoinedType() {
		return classType;
	}

}