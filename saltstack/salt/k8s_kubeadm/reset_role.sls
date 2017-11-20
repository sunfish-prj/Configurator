reset_kubernetes:
  cmd.run:
  - name: kubeadm reset
  grains.absent:
  - name: k8s_role
  - destructive: true
