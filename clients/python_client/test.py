"""
Simple test of cluster_prototype.salt_operations
"""
from pprint import pprint
from cluster_prototype import salt_operations
from cluster_prototype.salt_operations import SaltError


def main():
    """
    test
    """
    certauth = "saltmaster"
    master = "master"
    nodes = "node*"

    try:
        salt = salt_operations.SaltClient()
        salt.set_ca(certauth)
        salt.set_master(master)
        salt.set_node(nodes, master)
        salt.apply_labels(master)
        salt.deploy_yaml(master)
    except SaltError as exc:
        print("Error deploying cluster")
        pprint(exc.states_failed)
    else:
        print("Cluster deployed")


if __name__ == "__main__":
    main()
