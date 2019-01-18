package eu.nimble.indexing.repository.model.owl;

public interface INamed {
	String ID_FIELD = "id";
	/**
	 * Collection of languages
	 */
	String LANGUAGES_FIELD = "languages";
	/**
	 * The language based label, e.g. <code><i>en</i>_label</code> for english label
	 */
	String LABEL_FIELD = "*_label";
	/**
	 * Copy Field, language based. The language based label and description are stored in this field
	 * Final used index name is <code><i>en</i>_txt</code> for english text (label, description)
	 */
	String LANGUAGE_TXT_FIELD = "*_txt";
	/**
	 * Copy Field. All labels, descriptions are stored in this field
	 */
	String TEXT_FIELD = "_text_";
	/**
	 * The language based comment field, e.g.  <code><i>en</i>_comment</code> for english comments
	 */
	String COMMENT_FIELD = "*_comment";
	String NAME_SPACE_FIELD = "nameSpace";
	String LOCAL_NAME_FIELD = "localName"; 

}
