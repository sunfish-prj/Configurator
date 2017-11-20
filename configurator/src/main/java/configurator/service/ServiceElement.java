package configurator.service;

public class ServiceElement {
	
	private Integer serviceID;
	private Integer nodeID;
	
	
	public ServiceElement() {
	}
	
	public ServiceElement(Integer serviceID, Integer nodeID) {
		super();
		this.serviceID = serviceID;
		this.nodeID = nodeID;
	}
	public Integer getServiceID() {
		return serviceID;
	}
	public void setServiceID(Integer serviceID) {
		this.serviceID = serviceID;
	}
	public Integer getNodeID() {
		return nodeID;
	}
	public void setNodeID(Integer nodeID) {
		this.nodeID = nodeID;
	}
	
	

}
