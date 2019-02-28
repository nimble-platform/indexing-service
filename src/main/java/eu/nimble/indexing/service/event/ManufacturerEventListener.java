package eu.nimble.indexing.service.event;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import eu.nimble.indexing.repository.PartyRepository;
import eu.nimble.service.model.solr.party.PartyType;

@Component
public class ManufacturerEventListener implements ApplicationListener<ManufacturerEvent> {

	@Autowired
	private PartyRepository partyRepo;
	
	@Override
	public void onApplicationEvent(ManufacturerEvent event) {
	
		Optional<PartyType> pt = partyRepo.findById(event.getManufacturer().getId());
		if (! pt.isPresent() ) {
			// be sure to have the manufacturer in the index
			partyRepo.save(event.getManufacturer());
		}

		
	}

}
