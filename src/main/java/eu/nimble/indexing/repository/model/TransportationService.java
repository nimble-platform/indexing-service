//package eu.nimble.indexing.repository.model;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.solr.core.mapping.Indexed;
//import org.springframework.data.solr.core.mapping.SolrDocument;
//@SolrDocument(collection="item")
//public class TransportationService {
//	public static final String TYPE = "transportationService";
//	@Id
//	@Indexed
//	private String id;
//	
//	@Indexed(defaultValue=TYPE)
//	private String type = TYPE;
//
//	@Indexed
//	private String serviceType;
//	@Indexed
//	private String supportedProductNature;
//	@Indexed
//	private String supportedCargoType;
//	@Indexed(name="transportationMode")
//	private String transportationMode;
//	
//	@Indexed(type="pdouble")
//	private Double capacity;
//	@Indexed
//	private String capacityUnit;
//	@Indexed
//	private String emissionTypeCode;
//	@Indexed(type="pdouble")
//	private Double estimatedDuration;
//	@Indexed
//	private String estimatedDurationUnit;
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getServiceType() {
//		return serviceType;
//	}
//	public void setServiceType(String serviceType) {
//		this.serviceType = serviceType;
//	}
//	public String getSupportedProductNature() {
//		return supportedProductNature;
//	}
//	public void setSupportedProductNature(String supportedProductNature) {
//		this.supportedProductNature = supportedProductNature;
//	}
//	public String getSupportedCargoType() {
//		return supportedCargoType;
//	}
//	public void setSupportedCargoType(String supportedCargoType) {
//		this.supportedCargoType = supportedCargoType;
//	}
//	public String getTransportationMode() {
//		return transportationMode;
//	}
//	public void setTransportationMode(String transportationMode) {
//		this.transportationMode = transportationMode;
//	}
//	public Double getCapacity() {
//		return capacity;
//	}
//	public void setCapacity(Double capacity) {
//		this.capacity = capacity;
//	}
//	public String getCapacityUnit() {
//		return capacityUnit;
//	}
//	public void setCapacityUnit(String capacityUnit) {
//		this.capacityUnit = capacityUnit;
//	}
//	public String getEmissionTypeCode() {
//		return emissionTypeCode;
//	}
//	public void setEmissionTypeCode(String emissionTypeCode) {
//		this.emissionTypeCode = emissionTypeCode;
//	}
//	public Double getEstimatedDuration() {
//		return estimatedDuration;
//	}
//	public void setEstimatedDuration(Double estimatedDuration) {
//		this.estimatedDuration = estimatedDuration;
//	}
//	public String getEstimatedDurationUnit() {
//		return estimatedDurationUnit;
//	}
//	public void setEstimatedDurationUnit(String estimatedDurationUnit) {
//		this.estimatedDurationUnit = estimatedDurationUnit;
//	}
//	public String getType() {
//		return type;
//	}
//	public void setType(String type) {
//		this.type = type;
//	}
//}
