docker-pkg:
  lookup:
    # Docker version 1.11.2*, 1.12.6* tested
    version: 1.12.6*
k8s_cluster:
  # Token for cluster discovery. Used only when using kubeadm installation
  kubeadm_token: c76f85.ea93854078b5cb3
  # Kubernetes version
  k8s_version: v1.6.1
  # Container Network Interface version (v0.5.1 tested)
  CNI_version: v0.5.1
  apiserver:
    # Apiserver public ip secure port
    secure-port: 6443
  add-ons:
    dns:
      # Dns cluster service ip
      service_ip: 10.3.0.10
  # Ip range for the cluster services
  service_ip_range: 10.3.0.0/24
  # Pod network cidr
  pod-network-cidr: 10.2.0.0/16
mine_functions:
  k8s_role:
  - mine_function: grains.get
  - k8s_role
