package configurator.virtualmachine;

import java.util.List;

import configurator.exceptions.ConfiguratorItemAlreadyExistExist;
import configurator.exceptions.ConfiguratorItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class VirtualMachineController {

	@Autowired
	private VirtualMachineService vmService;

	@RequestMapping(path="/vms",method=RequestMethod.GET)
	public List<VirtualMachine> getAllVMs() throws ConfiguratorItemNotFoundException {
	    List<VirtualMachine> result = vmService.getAllVMs();
        if (result == null) {
            throw new ConfiguratorItemNotFoundException();
        }
		return result;
	}
	
	@RequestMapping(path="/vms/{id}",method=RequestMethod.GET)
	public VirtualMachine getVM(@PathVariable Integer id) throws ConfiguratorItemNotFoundException{
        VirtualMachine result = vmService.getVM(id);
        if (result == null) {
            throw new ConfiguratorItemNotFoundException();
        }
        return result;
	}

    @RequestMapping(value = "/vms", method = RequestMethod.POST)
	public ResponseEntity <String> postVM(@RequestBody @Valid VirtualMachine vm) throws ConfiguratorItemAlreadyExistExist {
	    VirtualMachine result = vmService.getVM(vm.getVmID());
	    if (result != null){
	        throw new ConfiguratorItemAlreadyExistExist();
        }
	    vmService.addVM(vm);
	    return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/vms/{id}", method = RequestMethod.PUT)
    public ResponseEntity <String> putVM(@PathVariable Integer id, @RequestBody @Valid VirtualMachine vm) throws ConfiguratorItemNotFoundException{
	    VirtualMachine oldVM = vmService.getVM(id);
	    if (oldVM == null){
	        throw new ConfiguratorItemNotFoundException();
        }
		vmService.modifyVM(id, vm);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/vms/{id}", method = RequestMethod.DELETE)
    public ResponseEntity <String> deleteVM(@PathVariable Integer id) throws ConfiguratorItemNotFoundException{
        VirtualMachine oldVM = vmService.getVM(id);
        if (oldVM == null){
            throw new ConfiguratorItemNotFoundException();
        }
        vmService.removeVM(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}