package configurator.virtualmachine;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

public class VirtualMachineRepositoryImpl implements VirtualMachineCustomRepository{

    private final MongoOperations operations;

    @Autowired
    public VirtualMachineRepositoryImpl(MongoOperations operations) {

        Assert.notNull(operations, "MongoOperations must not be null!");
        this.operations = operations;
    }

    @Override
    public void update(VirtualMachine vm) {
        Query query = new Query();
        query.addCriteria(Criteria.where("vmID").is(vm.getVmID()));
        DBObject dbObj = new BasicDBObject();
        operations.getConverter().write(vm, dbObj);
        Update update = new Update();
        for (String key : dbObj.keySet()) {
            Object value = dbObj.get(key);
            if(value!=null){
                update.set(key, value);
            }
        }
        operations.upsert(query, update, VirtualMachine.class);
    }
}