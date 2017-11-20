k8s_cluster:
    # List of nodes (name and ip) authorized to get a signed certificate by the ca (other than the apiserver)
  nodes:
    # Each node IP information can be assigned only to the node it refer and the CA
    master:
      ip: 192.168.71.101
    '1':
      ip: 192.168.71.102
    '2':
      ip: 192.168.71.103
  # Certificate authority minion name and folder of the public key
  ca:
    minion: saltmaster
    folder: /root/k8s_keys
  apiserver:
    # Apiserver minion
    minion: master
    # Apiserver public routable ip
    ip: 192.168.71.101
    # Apiserver cluster service ip
    service_ip: 10.3.0.1
