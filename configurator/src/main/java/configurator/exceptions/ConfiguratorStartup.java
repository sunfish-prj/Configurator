package configurator;

import com.suse.salt.netapi.exception.SaltException;
import configurator.exceptions.ConfiguratorStartupException;
import configurator.salt.SaltService;
import configurator.virtualmachine.VirtualMachine;
import configurator.virtualmachine.VirtualMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfiguratorStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private VirtualMachineService vmService;

    @Autowired
    private SaltService saltService;

    @Autowired
    private Environment environment;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        if (vmService.getVM(1) == null) {
            VirtualMachine vm = new VirtualMachine(1, "fresh", "1");
            vmService.addVM(vm);
        }
        if (vmService.getVM(2) == null) {
            VirtualMachine vm = new VirtualMachine(2, "fresh", "1");
            vmService.addVM(vm);
        }
        try {
            saltService.startupCluster();
        }
        catch ( SaltException exc) {
            logger.error(exc.getMessage());
            throw new ConfiguratorStartupException(exc);
        }
    }
}
