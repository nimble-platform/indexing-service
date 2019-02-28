package eu.nimble.indexing.service.event;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.service.model.solr.owl.PropertyType;
import eu.nimble.service.model.solr.owl.ValueQualifier;

@Component
public class PropertyMapEventListener implements ApplicationListener<PropertyMapEvent> {
	@Autowired
	private PropertyRepository propRepo;
	
	@Override
	public void onApplicationEvent(PropertyMapEvent event) {
		// collect the list of fieldNames
		Map<String, Set<String>> fieldNames = event.getFieldNames();
		// 
		List<PropertyType> existing = propRepo.findByItemFieldNamesIn(fieldNames.keySet());
		final Set<PropertyType> changed = new HashSet<>();
		synchronized(existing) {
			existing.forEach(new Consumer<PropertyType>() {
				
				@Override
				public void accept(PropertyType t) {
					boolean changeDetected = false;
					Set<String> idxNames = new HashSet<String>(t.getItemFieldNames()); 
					for (String fn : t.getItemFieldNames()) {
						if ( fieldNames.containsKey(fn)) {
							for (String idxField : fieldNames.get(fn)) {
								if ( ! idxNames.contains(idxField)) {
									idxNames.add(idxField);
									changeDetected = true;
								}
							}
						}
					}
					if ( changeDetected) {
						t.setValueQualifier(ValueQualifier.QUANTITY);
						t.setItemFieldNames(idxNames);
						changed.add(t);
					}
				}
			});
			
		}
		for ( PropertyType newPt : changed) {
			propRepo.save(newPt);
		}
	}
	

}
