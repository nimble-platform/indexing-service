package eu.nimble.indexing.repository.model;

import java.util.Collections;

import eu.nimble.indexing.repository.model.catalogue.PartyType;

public class PartyTypeUtils {
	public static PartyType template() {
		PartyType p = new PartyType();
		p.setId("uri");
		p.setName("name");
		p.setOrigin("origin");
		p.setCertificateType(Collections.singletonList("certificateType"));
		p.setPpapComplianceLevel("ppapComlianceLevel");
		p.setPpapDocumentType("ppapDocumentType");
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
