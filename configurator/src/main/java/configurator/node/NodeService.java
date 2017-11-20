package configurator.node;

import com.suse.salt.netapi.exception.SaltException;
import configurator.salt.SaltService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NodeService {

	@Autowired
	private NodeRepository nodeRepository;

	@Autowired
	private SaltService saltService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public List<Node> getAllNodes() {
		return nodeRepository.findAll();
	}

	public Node getNode(Integer nodeID) {
		return nodeRepository.findBynodeID(nodeID);
	}

	public void addNode(Node node) {
		try {
			saltService.addNode(node.getNodeID());
		}
		catch (SaltException exc) {
			logger.error(exc.getMessage());
		}
		nodeRepository.insert( node );
	}

	public void modifyNode(Integer id, Node node) {
		nodeRepository.update(node);
	}

	public void removeNode(Integer id) {
		try {
			saltService.removeNode(id);
		}
		catch (SaltException exc) {
			logger.error(exc.getMessage());
		}
		nodeRepository.deleteBynodeID(id);
	}

}
