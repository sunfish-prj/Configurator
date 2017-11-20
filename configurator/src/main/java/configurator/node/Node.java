package configurator.node;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "node")
public class Node {
	@NotNull
    @Indexed(unique = true)
	private Integer nodeID;
	@NotNull
	private Integer vmID;
	private List<String> label;
	
	public Node() {
		
	}
	
	public Node(Integer nodeID, Integer vmID, List<String> label) {
		super();
		this.nodeID = nodeID;
		this.vmID = vmID;
		this.label = label;
	}

	public Integer getNodeID() {
		return nodeID;
	}

	public void setNodeID(Integer nodeID) {
		this.nodeID = nodeID;
	}

	public Integer getVmID() {
		return vmID;
	}

	public void setVmID(Integer vmID) {
		this.vmID = vmID;
	}

	public List<String> getLabel() {
		return label;
	}

	public void setLabel(List<String> label) {
		this.label = label;
	}

}
