package configurator.virtualmachine;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Document(collection = "virtualMachine")
public class VirtualMachine {

    @NotNull
    @Indexed(unique = true)
	private Integer vmID;
	@NotNull
    @Pattern(regexp = "containerized|non-containerized|fresh")
	private String vmType;
	@NotNull
	private String confID;
	
	public VirtualMachine() {
	}
	
	public VirtualMachine(Integer vmID, String vmType, String confID) {
		super();
		this.vmID = vmID;
		this.vmType = vmType;
		this.confID = confID;
	}
	
	public Integer getVmID() {
		return vmID;
	}

	public void setVmID(Integer vmID) {
		this.vmID = vmID;
	}

	public String getVmType() {
		return vmType;
	}

	public void setVmType(String vmType) {
		this.vmType = vmType;
	}

	public String getConfID() {
		return confID;
	}

	public void setConfID(String confID) {
		this.confID = confID;
	}
	
}
