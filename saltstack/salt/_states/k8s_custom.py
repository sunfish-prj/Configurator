"""
Salt states for kubernetes management
"""


def label_node_present(name, value, node, kubeconfig=None):
    """
    Ensure the node node has the label with key name and value value

    name
        Name of the label
    value
        Value of the label
    node
        Node in which the label will be applied
    kubeconfig [optional]
        Path to the kubeconfig file
    """
    kubectl_arg = "label node " + node + " " + \
        name + "=" + value + " --overwrite"
    return __states__["cmd.run"]("kubectl " + kubectl_arg)


def node_is_present(name, kubeconfig=None):
    """
    Check if a node is present on the kubernetes cluster
    name
        Node name
    kubeconfig [optional]
        Path to the kubeconfig file
    """
    return __states__["cmd.run"]("kubectl get node " + name)


def node_cordoned(name, kubeconfig=None):
    """
    Make a node unschedulable
    name
        Node to be marked as unschedulable
    kubeconfig [optional]
        Path to the kubeconfig file
    """
    return __states__["cmd.run"]("kubectl cordon " + name)


def node_uncordoned(name, kubeconfig=None):
    """
    Make a node schedulable
    name
        Node to be marked as unschedulable
    kubeconfig [optional]
        Path to the kubeconfig file
    """
    return __states__["cmd.run"]("kubectl uncordon " + name)


def node_drained(name, kubeconfig=None):
    """
    Remove all pods from a node
    name
        Node to be drained
    kubeconfig [optional]
        Path to the kubeconfig file
    """
    kubectl_arg = "drain " + name + " --force --ignore-daemonsets"
    return __states__["cmd.run"]("kubectl " + kubectl_arg)


def node_absent(name, kubeconfig=None):
    """
    Remove a node from the kubernetes cluster
    name
        Node to be removed
    kubeconfig [optional]
        Path to the kubeconfig file
    """
    return __states__["cmd.run"]("kubectl delete node " + name)


def yaml_applied(name, kubeconfig=None):
    """
    Add all resources in a yaml file, if already present update them
    name
        Path to the yaml file to be applied, relative to /root/
    kubeconfig [optional]
        Path to the kubeconfig file
    """
    return __states__["cmd.run"]("kubectl apply -f " + name)
