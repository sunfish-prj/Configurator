package configurator.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class ServiceElementService {
	
	private List<ServiceElement> vms = Arrays.asList(
			new ServiceElement(1122,111),
			new ServiceElement(2233,222),
			new ServiceElement(4455,333)
			);
	
	public List<ServiceElement> getAllServices() {
		return vms;
	}
	
	public ServiceElement getServiceElement(Integer sid) {
		return vms.stream().filter(s -> s.getServiceID().equals(sid)).findFirst().get();
		
	}

}
