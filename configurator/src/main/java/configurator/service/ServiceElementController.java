package configurator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceElementController {
	
	@Autowired
	private ServiceElementService seService;
	
	@RequestMapping(path="/services",method=RequestMethod.GET)
	public List<ServiceElement> getAllVMs() {
		return seService.getAllServices();
	}
	
	@RequestMapping(path="/services/{id}",method=RequestMethod.GET)
	public ServiceElement getServiceElement(@PathVariable Integer id){
		return seService.getServiceElement(id);
	}

}
