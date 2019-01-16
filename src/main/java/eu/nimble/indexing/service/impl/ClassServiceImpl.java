package eu.nimble.indexing.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.ClassRepository;
import eu.nimble.indexing.repository.model.owl.ClassType;
import eu.nimble.indexing.service.ClassService;

@Service
public class ClassServiceImpl implements ClassService {
	// injected via Autowired
	private ClassRepository classRepo;
	
	@Override
	public ClassType getClass(String uri) {
		return classRepo.findById(uri).orElse(null);
	}
	@Override
	public void setClass(ClassType prop) {
		classRepo.save(prop);
		
	}
	@Override
	public void removeClass(String uri) {
		ClassType c = getClass(uri);
		if (c != null) {
			classRepo.delete(c);
		}
		
	}
	@Override
	public List<ClassType> getClasses(String forProperty) {
		return classRepo.findByProperties(forProperty);
	}
	
	@Autowired
	public void setClassRepository(ClassRepository repository) {
		this.classRepo = repository;
	}
	
}
