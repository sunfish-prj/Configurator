node_join:
  cmd.run:
  - name: kubeadm join --token {{ pillar['k8s_cluster']['kubeadm_token'] }} {{ salt['pillar.get']('k8s_cluster:apiserver:ip') }}
  grains.present:
  - name: k8s_role
  - value: node
