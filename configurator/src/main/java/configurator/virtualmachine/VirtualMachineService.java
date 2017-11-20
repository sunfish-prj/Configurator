package configurator.virtualmachine;
import java.util.List;

import com.suse.salt.netapi.exception.SaltException;
import configurator.exceptions.ConfiguratorItemNotFoundException;
import configurator.salt.SaltService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VirtualMachineService {

	@Autowired
	private VirtualMachineRepository vmRepository;

	@Autowired
    private SaltService saltService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public List<VirtualMachine> getAllVMs() {
		return vmRepository.findAll();
	}

	public VirtualMachine getVM(Integer vmID) {
        return vmRepository.findByvmID(vmID);
	}

	public void addVM(VirtualMachine vm) {
        vmRepository.insert( vm );
        return;
    }

    public void modifyVM(Integer id, VirtualMachine vm) {
		VirtualMachine oldVM = vmRepository.findByvmID(id);
		if ( !oldVM.getVmType().equals(vm.getVmType())) {
		    switch (vm.getVmType()) {
                case VmType.CONTAINERIZED:
		            try {
		                saltService.containerizedVM(id);
                    }
                    catch (SaltException exc) {
		                logger.error(exc.getMessage());
                    }
                    break;
                case VmType.NONCONTAINERIZED:
                    logger.error("Not implemented");
                    break;
            }
        }
	    vmRepository.update(vm);
    }

    public void removeVM(Integer id) throws ConfiguratorItemNotFoundException{
	    VirtualMachine vm = vmRepository.findByvmID(id);
	    if (vm == null) {
	        throw new ConfiguratorItemNotFoundException();
        }
	    vmRepository.deleteByvmID(id);
    }

}
