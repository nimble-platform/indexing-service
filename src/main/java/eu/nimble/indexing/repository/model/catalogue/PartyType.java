package eu.nimble.indexing.repository.model.catalogue;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection="manufacturer")
public class PartyType {

	@Id
	@Indexed(name="id")
	private String id;
	@Indexed
	private String name;
	@Indexed
	private String origin;
	@Indexed
	private Collection<String> certificateType;
	@Indexed
	private String ppapComplianceLevel;
	@Indexed
	private String ppapDocumentType;
	@Indexed(type="pdouble")
	private Double trustScore;
	@Indexed(type="pdouble")
	private Double trustRating;
	@Indexed(type="pdouble")
	private Double trustTradingVolume;
	@Indexed(type="pdouble")
	private Double trustSellerCommunication;
	@Indexed(type="pdouble")
	private Double trustFullfillmentOfTerms;
	@Indexed(type="pdouble")
	private Double trustDeliveryPackaging;
	@Indexed(type="pdouble")
	private Double trustNumberOfTransactions;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public Collection<String> getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(Collection<String> certificateType) {
		this.certificateType = certificateType;
	}
	public String getPpapComplianceLevel() {
		return ppapComplianceLevel;
	}
	public void setPpapComplianceLevel(String ppapComplianceLevel) {
		this.ppapComplianceLevel = ppapComplianceLevel;
	}
	public String getPpapDocumentType() {
		return ppapDocumentType;
	}
	public void setPpapDocumentType(String ppapDocumentType) {
		this.ppapDocumentType = ppapDocumentType;
	}
	public Double getTrustScore() {
		return trustScore;
	}
	public void setTrustScore(Double trustScore) {
		this.trustScore = trustScore;
	}
	public Double getTrustRating() {
		return trustRating;
	}
	public void setTrustRating(Double trustRating) {
		this.trustRating = trustRating;
	}
	public Double getTrustTradingVolume() {
		return trustTradingVolume;
	}
	public void setTrustTradingVolume(Double trustTradingVolume) {
		this.trustTradingVolume = trustTradingVolume;
	}
	public Double getTrustSellerCommunication() {
		return trustSellerCommunication;
	}
	public void setTrustSellerCommunication(Double trustSellerCommunication) {
		this.trustSellerCommunication = trustSellerCommunication;
	}
	public Double getTrustFullfillmentOfTerms() {
		return trustFullfillmentOfTerms;
	}
	public void setTrustFullfillmentOfTerms(Double trustFullfillmentOfTerms) {
		this.trustFullfillmentOfTerms = trustFullfillmentOfTerms;
	}
	public Double getTrustDeliveryPackaging() {
		return trustDeliveryPackaging;
	}
	public void setTrustDeliveryPackaging(Double trustDeliveryPackaging) {
		this.trustDeliveryPackaging = trustDeliveryPackaging;
	}
	public Double getTrustNumberOfTransactions() {
		return trustNumberOfTransactions;
	}
	public void setTrustNumberOfTransactions(Double trustNumberOfTransactions) {
		this.trustNumberOfTransactions = trustNumberOfTransactions;
	}

}
