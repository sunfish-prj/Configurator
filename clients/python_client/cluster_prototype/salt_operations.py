"""
Cluster saltstack operations module
"""
import salt.client


class SaltClient:
    """
    Client class for salt operations
    """
    def __init__(self):
        """
        Create salt LocalClient
        """
        self._local = salt.client.LocalClient()

    def set_ca(self, target):
        """
        Set a minion as kubernetes cluster ca

        target
            Minion to set as kubernetes cluster ca
        """
        ret = self._local.cmd(
            target,
            "state.apply", ["k8s.ca"])
        _check_return_values(ret)
        return

    def set_master(self, target):
        """
        Set a minion as kubernetes cluster master

        target
            Minion to set as kubernetes cluster master
        """
        ret = self._local.cmd(
            target,
            "state.apply", ["docker"])
        _check_return_values(ret)
        ret = self._local.cmd(
            target,
            "state.apply", ["k8s"])
        _check_return_values(ret)
        ret = self._local.cmd(
            target,
            "state.apply",
            ["k8s.master"])
        _check_return_values(ret)

    def set_node(self, target, master):
        """
        Set a minion as kubernetes cluster node

        target
            Minion to set as kubernetes cluster node
        master
            Minion already set up as kubernetes cluster master
        """
        ret = self._local.cmd(
            target,
            "state.apply",
            ["docker"])
        _check_return_values(ret)
        ret = self._local.cmd(
            target,
            "state.apply",
            ["k8s"])
        _check_return_values(ret)
        ret = self._local.cmd(
            target,
            "state.apply",
            ["k8s.node"])
        _check_return_values(ret)
        node_set = ret
        for node in node_set.iterkeys():
            ret = self._local.cmd(
                master,
                "state.single",
                ["k8s_custom.node_is_present"],
                kwarg={"name": node})
            _check_return_values(ret)

    def apply_labels(self, target):
        """
        Apply labels to the kubernetes cluster

        target
            Minion already set up as kubernetes cluster master
        """
        ret = self._local.cmd(
            target,
            "state.apply",
            ["k8s.master.node_label"])
        _check_return_values(ret)

    def deploy_yaml(self, target):
        """
        Deploy yaml in the kubernetes cluster

        target
            Minion already set up as kubernetes cluster master
        """
        ret = self._local.cmd(
            target,
            "state.apply",
            ["k8s.master.deploy_yaml"])
        _check_return_values(ret)

    def reset_role_node(self, target, master):
        """
        Reset a node already part of kubernetes (detach it from the cluster
        and reset it)

        target
            Minion to be reset
        master
            Minion already set up as kubernetes cluster master
        """
        ret = self._local.cmd(
            master,
            "state.single",
            ["k8s_custom.node_drained"],
            kwarg={"name": target})
        _check_return_values(ret)
        ret = self._local.cmd(
            master,
            "state.single",
            ["k8s_custom.node_absent"],
            kwarg={"name": target})
        _check_return_values(ret)
        ret = self._local.cmd(
            target,
            "state.apply",
            ["k8s.reset_role"])
        _check_return_values(ret)


def _check_return_values(ret):
    """
    Check that all states are succesfully applied
    Raise a SaltError custom exception if not
    ret
        Return values to be checked
    """
    states_failed = []
    for states in ret.itervalues():
        for state, state_result in states.iteritems():
            if state_result["result"] is False:
                states_failed.append(state)
    if states_failed:
        raise SaltError("Failed applying states", states_failed)


class SaltError(Exception):
    """
    Exception for Salt errors while applying states
    """
    def __init__(self, message, states_failed):
        super(SaltError, self).__init__(message)
        self.states_failed = states_failed
