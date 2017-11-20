master_init:
  cmd.run:
  - name: kubeadm init --token={{ pillar['k8s_cluster']['kubeadm_token'] }} --pod-network-cidr={{ pillar['k8s_cluster']['pod-network-cidr'] }} --api-advertise-addresses={{ salt['pillar.get']('k8s_cluster:apiserver:ip') }}
set_kubectl:
  file.copy:
  - name: /root/.kube/config
  - source: /etc/kubernetes/admin.conf
  - makedirs: true
deploy_flannel:
  file.managed:
  - name: /root/flannel.yaml
  - source: salt://k8s/files/flannel.yaml
  - template: jinja
  cmd.run:
  - name: kubectl apply -f /root/flannel.yaml
clean_up_flannel_yaml:
  file.absent:
  - name: /root/flannel.yaml
set_k8s_role_grain:
  grains.present:
  - name: k8s_role
  - value: master
