package eu.nimble.indexing.service;

import java.util.Set;

import eu.nimble.service.model.solr.SearchResult;
import eu.nimble.service.model.solr.owl.PropertyType;

public interface PropertyService extends SolrService<PropertyType> {

	SearchResult<PropertyType> findByUris(Set<String> uriSet);

	SearchResult<PropertyType> findForNamespaceAndLocalNames(String nameSpace, Set<String> localNames);

	SearchResult<PropertyType> findForClass(String classType);

	SearchResult<PropertyType> findByIdxNames(Set<String> idxNames);


}
