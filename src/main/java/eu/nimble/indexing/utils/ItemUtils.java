package eu.nimble.indexing.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ext.com.google.common.base.CaseFormat;
import org.springframework.util.StringUtils;

import eu.nimble.service.model.solr.item.ItemType;
import eu.nimble.service.model.solr.owl.PropertyType;

public class ItemUtils {

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
//		String fq = "manufacturer.trustScore:[1 TO 4]";
		ItemType t = template();
		System.out.println(t.getMultiLingualProperty("qualifiedMulti", "de"));
		t.getPrice();
	}

	public static String encode(String in) {
		try {
			return URLEncoder.encode(in, "utf8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return in;
		}
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
		item.addLabel("en", "English Name");
		item.addLabel("es", "Espana");
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
		item.addProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#hasLength", "cm", 200.0);
		item.addProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#hasColour", "blue");
		item.addProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#hasColour", "red");
		item.addProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#hasUnitsPerPack", 10.0);
		
		item.addProperty("length", "cm", 20.0);
		item.setProperty("http://www.aidimme.es/FurnitureSectorTaxonomy.owl#isFireProof", Boolean.TRUE);
		PropertyType c = new PropertyType();
		c.addLabel("en", "English label for custom property");
		c.addComment("en", "a comment for this particular custom property");
		c.addDescription("en", "some description for the custom property");
		item.addProperty("reallyCustom", 3.1415, c);
		
		PropertyType cq = new PropertyType();
		cq.addLabel("en", "English label for a qualified custom property");
		cq.addComment("en", "a comment for this particular custom property");
		cq.addDescription("en", "some description for the custom property");
		item.addProperty("qualifiedCustom", "km", 20.0, cq);
		item.addProperty("qualifiedCustom",  "m", 500.0, cq);
		item.addMultiLingualProperty("qualifiedMulti", "en", "blue");
		item.addMultiLingualProperty("qualifiedMulti", "de", "blau");
		
		item.getMultiLingualProperty("qualifiedMulti", "en");
		return item;

	}
	private static <T> List<T> asList(@SuppressWarnings("unchecked") T ...ts ) {
		List<T> set = new ArrayList<T>();
		for ( T t : ts) {
			set.add(t);
		}
		return set;
	}
}
