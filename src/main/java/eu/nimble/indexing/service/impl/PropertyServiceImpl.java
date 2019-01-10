package eu.nimble.indexing.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.PropertyRepository;
import eu.nimble.indexing.repository.model.owl.Property;
import eu.nimble.indexing.service.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {
	// injected via Autowired
	private PropertyRepository propRepo;
	
	@PostConstruct
	public void init() {
		
	}
	@Override
	public Property getProperty(String uri) {
		// 
		List<Property> props = propRepo.findByUri(uri);//.orElse(null);
		if (props.size() > 0) {
			return props.get(0);
		}
		return null;
	}

	@Override
	public void setProperty(Property prop) {
		propRepo.save(prop);
	}

	@Override
	public void removeProperty(String uri) {
		Property prop  = getProperty(uri);
		if ( prop != null) {
			propRepo.delete(prop);
		}
	}

	@Autowired
	public void setPropertyRepository(PropertyRepository repository) {
		this.propRepo = repository;
	}

	@Override
	public List<Property> getProperties(String forClass) {
		return propRepo.findByProduct(forClass);
	}
}
