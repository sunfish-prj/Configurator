"""
Salt kubernetes module implemented with kubernetes python client
https://github.com/kubernetes-incubator/client-python
Require python packages:
kubernetes
urllib3 > 1.20
pyYAML
"""

from __future__ import absolute_import

import logging
import copy
import yaml
from kubernetes import client, config
from kubernetes.client.rest import ApiException


# TODO NOT WORKING (PARSING ERROR)
def label_node_set(name, value, node, kubeconfig=None):
    """
    Start the kubelet service and ensure the apiserver is running

    name
        Name of the label
    value
        Value of the label
    node
        Node in which the label will be applied
    config
        Path to the kubeconfig file
    """
    ret = {
        "name": name,
        "changes": {},
        "result": False,
        "comment": "",
    }
    log = logging.getLogger(__name__)
    _load_config(kubeconfig)
    api_instance = client.CoreV1Api()
    node_instance = api_instance.read_node(
        node,
        exact=False,
        export=True)
    node_copy = copy.deepcopy(node_instance)
    node_labels = node_instance.metadata.labels
    if node_labels is None:
        node_labels = {}
    node_labels[name] = value
    node_instance.status = None
    node_modified = api_instance.replace_node(name=node, body=node_instance)
    ret["changes"] = {"old": node_copy, "new": node_modified}
    ret["result"] = True
    ret["comment"] = "Node labels modified"
    return ret


def node_check(name, kubeconfig=None):
    """
    Check that node name is present in the kubernetes cluster

    name
        Node name to be checked
    kubeconfig
        Path to the kubeconfig file
    """
    ret = {
        "name": name,
        "changes": {},
        "result": False,
        "comment": "",
    }
    log = logging.getLogger(__name__)
    _load_config(kubeconfig)
    api_instance = client.CoreV1Api()
    try:
        api_instance.read_node(
            name=name,
            exact=True,
            export=True)
    except ApiException as exc:
        if exc.status == 404:
            ret["result"] = False
            ret["comment"] = "Node " + name + " is not present"
            return ret
        else:
            log.warning("Error checking node. http response: " + exc.status)
            ret["result"] = False
            ret["comment"] = "Error checking node. http response: " + exc.status
            return ret
    ret["result"] = True
    ret["comment"] = "Node " + name + " is present"
    return ret


# TODO Error checking, more resources types
def deploy_yaml(name, namespace="default", kubeconfig=None):
    """
    Deploy all resources of a yaml file in the kubernetes cluster
    Only service, deployment, pod, DaemonSet kind of resources are supported

    name
        Path to the yaml file
    kubeconfig
        Path to the kubeconfig file
    """
    ret = {
        "name": name,
        "changes": {},
        "result": False,
        "comment": "",
    }
    log = logging.getLogger(__name__)
    ret_array = []
    with open(name) as yaml_file:
        res = yaml.load_all(stream=yaml_file)
        for data in res:
            log.warning(data)
            if data["kind"] == "Service":
                ret_array.append(
                    _deploy_service(data, namespace, kubeconfig))
            elif data["kind"] == "Deployment":
                ret_array.append(
                    _deploy_deployment(data, namespace, kubeconfig))
            elif data["kind"] == "Pod":
                ret_array.append(
                    _deploy_pod(data, namespace, kubeconfig))
            elif data["kind"] == "DaemonSet":
                ret_array.append(
                    _deploy_daemonset(data, namespace, kubeconfig))
    ret["result"] = True
    ret["comment"] = "Deployment succesful"
    for result in ret_array:
        if result["result"] is False:
            ret["result"] = False
            ret["comment"] = "Deployment failed"
    return ret


# TODO Case when kubeconfig path is specified
def _load_config(kubeconfig=None):
    if kubeconfig is None:
        config.load_kube_config()
    else:
        config.load_kube_config()
    return


def _deploy_service(data, namespace, kubeconfig):
    ret = {
        "changes": {},
        "result": False,
    }
    _load_config(kubeconfig)
    api_instance = client.CoreV1Api()
    api_instance.create_namespaced_service(
        body=data,
        namespace=namespace)
    ret["result"] = True


def _deploy_deployment(data, namespace, kubeconfig):
    ret = {
        "changes": {},
        "result": False,
    }
    _load_config(kubeconfig)
    api_instance = client.ExtensionsV1beta1Api()
    api_instance.create_namespaced_deployment(
        body=data,
        namespace=namespace)
    ret["result"] = True


def _deploy_pod(data, namespace, kubeconfig):
    ret = {
        "changes": {},
        "result": False,
    }
    _load_config(kubeconfig)
    api_instance = client.CoreV1Api()
    api_instance.create_namespaced_pod(
        body=data,
        namespace=namespace)
    ret["result"] = True


def _deploy_daemonset(data, namespace, kubeconfig):
    ret = {
        "changes": {},
        "result": False,
    }
    _load_config(kubeconfig)
    api_instance = client.ExtensionsV1beta1Api()
    api_instance.create_namespaced_daemon_set(
        body=data,
        namespace=namespace)
    ret["result"] = True
