package eu.nimble.indexing.service;

public interface OntologyService {
	
	public void upload(String mimeType, String onto);

	boolean deleteNamespace(String namespace);

}
