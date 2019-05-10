package eu.nimble.indexing.service;

import java.util.List;

public interface OntologyService {



    public void upload(String mimeType, List<String> nameSpaces, String onto);

	boolean deleteNamespace(String namespace);
	


}
