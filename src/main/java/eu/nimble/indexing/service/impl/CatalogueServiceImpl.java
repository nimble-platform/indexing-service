package eu.nimble.indexing.service.impl;

import java.util.Iterator;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;

import eu.nimble.indexing.repository.AdditionalPropertyRepository;
import eu.nimble.indexing.repository.ItemRepository;
import eu.nimble.indexing.repository.model.catalogue.AdditionalProperty;
import eu.nimble.indexing.repository.model.catalogue.ItemType;
import eu.nimble.indexing.service.CatalogueService;

@Service
public class CatalogueServiceImpl implements CatalogueService {
	private Logger logger = LoggerFactory.getLogger(CatalogueServiceImpl.class);

	private AdditionalPropertyRepository propRepo;
	private ItemRepository itemRepo;
	


	@Autowired
	public void setAdditionalPropertyRepository(AdditionalPropertyRepository repository) {
		this.propRepo = repository;
	}
	@Autowired
	public void setItemRepository(ItemRepository itemRepo) {
		this.itemRepo = itemRepo;
		
	}
	@Resource
	private SolrTemplate solrTemplate;
	
	@Override
	public ItemType getItem(String uri) {
		return itemRepo.findByUri(uri);
	}

	@Override
	public void setItem(ItemType item) {
		itemRepo.save(item);
		
	}

	@Override
	public void removeItem(String uri) {
		ItemType item = getItem(uri);
		
		// delete the item when found
		if (item!=null) {
			// explicitly remove the nested documents
			Iterator<AdditionalProperty> p = item.getAdditionalProperty().iterator();
			while (p.hasNext()) {
				propRepo.delete(p.next());
			}
			// remove the item
			itemRepo.delete(item);
		}		
	}
	
	@Override
	public FacetPage<ItemType> search(String query, Pageable pageable) {
		return itemRepo.findItemByName(query, "en", pageable);
	}

}
