package eu.nimble.indexing.solr.query;

import java.util.Optional;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleField;

/**
 * Construct a {@link Field} for the field projectionlist, eg. use
 * with {@link Query#addProjectionOnField(Field)}.
 * <p>
 * The field is created with <pre>[child parentFilter="criteria">]</pre>
 * where the criteria denotes the type of the parent document such as 
 * <pre>doctype:parentDocType</pre>
 * </p>
 * @author dglachs
 *
 */
public class ParentFilterField extends SimpleField {

	private final FilterQuery parentFilter;
	public ParentFilterField(FilterQuery crit) {
		super(crit.getCriteria().getField().getName());
		this.parentFilter = crit;
	}
	public String getName() {
		Criteria c = parentFilter.getCriteria();
		Optional<Criteria.Predicate> val = c.getPredicates().stream().findFirst();
		if ( val.isPresent()) {
			return String.format("[child parentFilter=%s]", super.getName()+":" +val.get().getValue().toString());
		}
		return super.getName();	
	}
}
