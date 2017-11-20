k8s_cluster:
  etcd:
    # Etcd database version
    version: v3.1.4
  apiserver:
    # Apiserver insecure port, accesible only on local
    insecure-port: 8080
  # Network interface in which flannel will operate. Valid for all nodes. Optional. Default: first network interface
  flannel-iface: enp0s8
  # Label to be applied
  node_labels:
  - node: master
    key: id
    value: M
  - node: '1'
    key: id
    value: N1
  - node: '2'
    key: id
    value: N2
  # Yaml to be deployed on the cluster, stored on k8s_yaml folder
  deployments:
  - file: node1_pod_svc.yaml
  - file: DeploymentExample.yaml
  - file: busybox.yaml
