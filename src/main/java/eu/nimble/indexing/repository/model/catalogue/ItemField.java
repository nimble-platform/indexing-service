package eu.nimble.indexing.repository.model.catalogue;

public enum ItemField {
	CATALOG_ID("id")
	
	;
	private String fieldName;
	ItemField(String indexName ) {
		this.fieldName = indexName;
	}

}
