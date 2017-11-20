package configurator.node;

import java.util.List;

import configurator.exceptions.ConfiguratorItemAlreadyExistExist;
import configurator.exceptions.ConfiguratorItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NodeController {


	@Autowired
	private NodeService nodeService;

	@RequestMapping(path="/nodes", method=RequestMethod.GET)
	public List<Node> getAllNodes() throws ConfiguratorItemNotFoundException{
		List<Node> result = nodeService.getAllNodes();
		if (result == null) {
			throw new ConfiguratorItemNotFoundException();
		}
		return result;
	}

	@RequestMapping(path="/nodes/{id}", method=RequestMethod.GET)
	public Node getNode(@PathVariable Integer id) throws ConfiguratorItemNotFoundException{
		Node result = nodeService.getNode(id);
		if (result == null) {
			throw new ConfiguratorItemNotFoundException();
		}
		return result;
	}

	@RequestMapping(value = "/nodes", method = RequestMethod.POST)
	public ResponseEntity<String> postVM(@RequestBody Node node) throws ConfiguratorItemAlreadyExistExist {
		Node result = nodeService.getNode(node.getNodeID());
		if (result != null){
			throw new ConfiguratorItemAlreadyExistExist();
		}
		nodeService.addNode(node);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@RequestMapping(value = "/nodes/{id}", method = RequestMethod.PUT)
	public ResponseEntity <String> putVM(@PathVariable Integer id, @RequestBody Node node) throws ConfiguratorItemNotFoundException{
		Node oldNode = nodeService.getNode(id);
		if (oldNode == null){
			throw new ConfiguratorItemNotFoundException();
		}
		nodeService.modifyNode(id, node);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@RequestMapping(value = "/nodes/{id}", method = RequestMethod.DELETE)
	public ResponseEntity <String> deleteVM(@PathVariable Integer id) throws ConfiguratorItemNotFoundException {
		Node oldVM = nodeService.getNode(id);
		if (oldVM == null){
			throw new ConfiguratorItemNotFoundException();
		}
		nodeService.removeNode(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
