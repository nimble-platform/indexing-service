package eu.nimble.indexing.repository.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ext.com.google.common.base.CaseFormat;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.util.StringUtils;

import eu.nimble.indexing.repository.model.catalogue.AdditionalProperty;
import eu.nimble.indexing.repository.model.catalogue.ICatalogueItem;
import eu.nimble.indexing.repository.model.catalogue.IParty;
import eu.nimble.indexing.repository.model.catalogue.ItemType;

public class ItemUtils implements ICatalogueItem {
	public static String dynamicFieldPart(String fieldPart) {
		if (! StringUtils.hasText(fieldPart)) {
			// when no unit code specified - use "undefined";
			return "undefined";
		}
		String dynamicFieldPart = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldPart);
		dynamicFieldPart = dynamicFieldPart.replaceAll("[^a-zA-Z0-9_ ]", "");
		dynamicFieldPart = dynamicFieldPart.trim().replaceAll(" ", "_").toUpperCase();
		dynamicFieldPart = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, dynamicFieldPart);
		return dynamicFieldPart;
		
	}
	public static ItemType template() {
		ItemType item = new ItemType();
		item.setUri("uri");
		item.addName("en", "English Name");
		item.addName("es", "Espana");
		item.addDescription("en", "English desc");
		item.setCatalogueId("cat_id");
		item.setEmissionStandard("emission");
		item.setFreeOfCharge(false);
		item.addPrice("EUR", 100.00);
		item.addPrice("USD", 110.00);
		item.addPackageAmounts("Palette(s)", asList(30.0, 15.0));
		item.setCatalogueId("Euro");
		item.setManufacturerId("manu_001");
		item.addDeliveryTime("Week(s)", 2.0);
		item.addDeliveryTime("Day(s)", 14.0);
		AdditionalProperty add = new AdditionalProperty();
		add.setId("uri-prop-01");
		// 
		add.addName("en", "Property Name");
		add.addName("es", "Prop nom");
		// 
		add.addValue("Month(s)", 3.0);
		add.addValue("Day(s)", 60.0);
		//
		add.setValueQualifier("quantity");
		// 
		item.getAdditionalProperty().add(add);
		return item;

	}
	private static <T> List<T> asList(@SuppressWarnings("unchecked") T ...ts ) {
		List<T> set = new ArrayList<T>();
		for ( T t : ts) {
			set.add(t);
		}
		return set;
	}
	public static SimpleFilterQuery doctypeFilter() {
		return new SimpleFilterQuery(Criteria.where(TYPE_FIELD).is(TYPE_VALUE));
	}
	public static SimpleFilterQuery nestedFieldFilter(String field, String query) {
		Criteria crit = Criteria.where(String.format("{!parent which=%s:%s} %s", TYPE_FIELD, TYPE_VALUE, field));
		return new SimpleFilterQuery(crit.expression(query));
	}
	public static SimpleFilterQuery filterManufacturerField(String queryField, String query) {
		Criteria crit = Criteria.where(String.format("{!join from=%s to=%s fromIndex=%s} %s", IParty.ID_FIELD, MANUFACTURER_ID_FIELD, IParty.COLLECTION_NAME, queryField));
		return new SimpleFilterQuery(crit.expression(query));
		
	}
}
