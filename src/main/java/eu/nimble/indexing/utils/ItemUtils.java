package eu.nimble.indexing.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jena.ext.com.google.common.base.CaseFormat;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.util.StringUtils;

import eu.nimble.service.model.solr.item.ICatalogueItem;
import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.party.IParty;

public class ItemUtils implements ICatalogueItem {
//	public static String dynamicFieldPart(String ...strings ) {
//		StringBuilder sb = new StringBuilder("");
//		for (String s : strings) {
//			sb.append(s+"_");
//		}
//		return dynamicFieldPart(sb.toString());
//	}
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
	public static void main(String [] args) {
		
		String template = "*_xminute";
		String qualified = "mixture_xminute";
		
		String template1 = "prefix_*";
		String qualitfied1 = "prefix_prefix_with_something";
		System.out.println(extractFromTemplate(qualified, template));
		System.out.println(extractFromTemplate(qualitfied1, template1));
		System.out.println(dynamicFieldPart("http://www.aidimme.es./FurnitureSectorTaxonomy.owl#hasItemPerPack"));
		
		ItemType t = template();
		t.getPrice();
	}
	public static String extractFromTemplate(String qualified, String template) {
		int starPos = template.indexOf("*");
		
		if (! (starPos < 0)) {
			boolean leadingStar = template.startsWith("*");
			String strippedWildcard = template.replace("*", "");
			
			if ( leadingStar) {
				if ( qualified.endsWith(strippedWildcard)) {
					return qualified.substring(0, qualified.length() - strippedWildcard.length());
				}
			}
			else {
				if ( qualified.startsWith(strippedWildcard)) {
					return qualified.substring(strippedWildcard.length());
				}
			}
		}
		return null;
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
		// 
		item.addProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#hasColour", "blue");
		item.addProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#hasColour", "red");
		item.addProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#hasUnitsPerPack", 10.0);
		
		item.addProperty("length", "cm", 20.0);
		item.setProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#isFireProof", Boolean.TRUE);
		
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
//	public static SimpleFilterQuery nestedFieldFilter(String field, String query) {
//		Criteria crit = Criteria.where(String.format("{!parent which=%s:%s} %s", TYPE_FIELD, TYPE_VALUE, field));
//		return new SimpleFilterQuery(crit.expression(query));
//	}
	public static SimpleFilterQuery filterManufacturerField(String queryField, String query) {
		Criteria crit = Criteria.where(String.format("{!join from=%s to=%s fromIndex=%s} %s", IParty.ID_FIELD, MANUFACTURER_ID_FIELD, IParty.COLLECTION_NAME, queryField));
		return new SimpleFilterQuery(crit.expression(query));
		
	}
}
