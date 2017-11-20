include:
- k8s.cert

private_key:
  x509.private_key_managed:
  - name: /etc/kubernetes/ssl/node-key.pem
  - bits: 4096

public_key:
  x509.certificate_managed:
    - name: /etc/kubernetes/ssl/node.pem
    - ca_server: {{ pillar['k8s_cluster']['ca']['minion']}}
    - signing_policy: {{ grains['fqdn']}}
    - public_key: /etc/kubernetes/ssl/node-key.pem
    - CN: {{ grains['fqdn']}}
    - days_remaining:
    - require:
      - x509: get_ca_pem

clean_kubelet_settings:
  file.absent:
  - name: /etc/systemd/system/kubelet.service.d

set_node_kubeconfig:
  file.managed:
  - name: /etc/kubernetes/node-kubeconfig.yaml
  - source: salt://k8s/files/node/node-kubeconfig.yaml
  - template: jinja

set_kubelet:
  file.managed:
  - name: /etc/systemd/system/kubelet.service
  - source: salt://k8s/files/node/services/kubelet.service
  - template: jinja

create_manifest_dir:
  file.directory:
  - name: /etc/kubernetes/manifests
  - makedirs: true

set_kube-proxy_pod:
  file.managed:
  - name: /etc/kubernetes/manifests/kube-proxy.yaml
  - source: salt://k8s/files/node/manifests/kube-proxy.yaml
  - template: jinja

reload_services:
  module.run:
  - name: service.systemctl_reload

start_kubelet:
  service.running:
  - name: kubelet
  - enable: True
  - failhard: True
  - watch:
    - file: /etc/systemd/system/kubelet.service
  file.managed:
  - name: /etc/systemd/system/kubelet.service
  - source: salt://k8s/files/node/services/kubelet.service
  - template: jinja

set_k8s_role_grain:
  grains.present:
  - name: k8s_role
  - value: node

mine_update:
  module.run:
  - name: mine.update
