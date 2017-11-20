package configurator.node;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NodeRepository extends MongoRepository<Node, String>, NodeCustomRepository {
    public Node findBynodeID(Integer nodeID);
    public void deleteBynodeID(Integer nodeID);
}

