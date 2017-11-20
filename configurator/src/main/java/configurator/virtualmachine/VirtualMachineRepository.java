package configurator.virtualmachine;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VirtualMachineRepository extends MongoRepository<VirtualMachine, String>, VirtualMachineCustomRepository{
    public VirtualMachine findByvmID(Integer vmID);
    public void deleteByvmID(Integer vmID);
}

