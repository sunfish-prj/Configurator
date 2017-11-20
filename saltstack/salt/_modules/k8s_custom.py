"""
Salt kubernetes module implemented with kubectl
"""


def get_node_list():
    """
    Get list of nodes in the cluster as json
    """
    return  __salt__["cmd.run"]("kubectl get nodes -o=json")


def get_pods_list(namespace=None):
    """
    Get list of pods as json

    namespace
        namespace of the pods. Default all namespaces
    """
    if namespace is None:
        return __salt__["cmd.run"]("kubectl get pods -o=json --all-namespaces")
    return __salt__["cmd.run"]("kubectl get pods -o=json -n=" + namespace)


def get_svc_list(namespace=None):
    """
    Get list of pods as json

    namespace
        namespace of the pods. Default all namespaces
    """
    if namespace is None:
        return __salt__["cmd.run"]("kubectl get svc -o=json --all-namespaces")
    return __salt__["cmd.run"]("kubectl get svc -o=json -n=" + namespace)
