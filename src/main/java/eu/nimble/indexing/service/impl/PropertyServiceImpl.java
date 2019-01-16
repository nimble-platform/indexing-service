package eu.nimble.indexing.service.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.repository.model.owl.PropertyType;
import eu.nimble.indexing.service.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {
	// injected via Autowired
	private PropertyRepository propRepo;
	
	@PostConstruct
	public void init() {
		
	}
	@Override
	public PropertyType getProperty(String uri) {
		// 
		List<PropertyType> props = propRepo.findByUri(uri);//.orElse(null);
		if (props.size() > 0) {
			return props.get(0);
		}
		return null;
	}

	@Override
	public void setProperty(PropertyType prop) {
		propRepo.save(prop);
	}

	@Override
	public void removeProperty(String uri) {
		PropertyType prop  = getProperty(uri);
		if ( prop != null) {
			propRepo.delete(prop);
		}
	}

	@Autowired
	public void setPropertyRepository(PropertyRepository repository) {
		this.propRepo = repository;
	}

	@Override
	public List<PropertyType> getProperties(String forClass) {
		return propRepo.findByProduct(forClass);
	}
	@Override
	public List<PropertyType> getPropertiesByName(List<String> names) {
		return propRepo.findByLocalNameIn(names);
	}
	@Override
	public List<PropertyType> getPropertiesByUri(List<String> uri) {
		return propRepo.findByUriIn(uri);
	}
	@Override
	public List<PropertyType> getPropertiesByIndexName(Set<String> names) {
		return propRepo.findByItemFieldNamesIn(names);
	}
	
}
