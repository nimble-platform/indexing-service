package eu.nimble.indexing.utils;

import eu.nimble.service.model.solr.party.PartyType;

public class PartyTypeUtils {
	public static PartyType template() {
		PartyType p = new PartyType();
		p.setId("uri");
		p.setLegalName("legalName");
		p.addLabel("en","name");
		p.addOrigin("en","origin");
		p.setPpapComplianceLevel(3);
		p.addPpapDocumentType("en","ppapDocumentType");
		p.setTrustDeliveryPackaging(0.0d);
		p.setTrustFullfillmentOfTerms(0.0d);
		p.setTrustNumberOfTransactions(0.0d);
		p.setTrustRating(0.0d);
		p.setTrustScore(0.0d);
		p.setTrustSellerCommunication(0.0d);
		p.setTrustTradingVolume(0.0d);
		return p;
	}
}
