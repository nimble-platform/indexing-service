package eu.nimble.indexing.repository.model;

import org.apache.jena.ext.com.google.common.base.CaseFormat;
import org.springframework.util.StringUtils;

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
}
