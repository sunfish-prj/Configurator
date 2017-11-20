include:
- k8s.cert

sync_custom_module:
  module.run:
  - name: saltutil.sync_all

apiserver_private_key:
  x509.private_key_managed:
  - name: /etc/kubernetes/ssl/apiserver-key.pem
  - bits: 4096

apiserver_public_key:
  x509.certificate_managed:
    - name: /etc/kubernetes/ssl/apiserver.pem
    - ca_server: {{ pillar['k8s_cluster']['ca']['minion']}}
    - signing_policy: apiserver
    - public_key: /etc/kubernetes/ssl/apiserver-key.pem
    - CN: kube-apiserver
    - days_remaining: 30
    - require:
      - x509: get_ca_pem

set_etcd:
  file.managed:
  - name: /etc/systemd/system/docker-etcd.service
  - source: salt://k8s/files/master/services/docker-etcd.service
  - template: jinja
  service.running:
  - name: docker-etcd
  - enable: true
  - failhard: True

clean_kubelet_settings:
  file.absent:
  - name: /etc/systemd/system/kubelet.service.d

set_master_kubeconfig:
  file.managed:
  - name: /etc/kubernetes/master-kubeconfig.yaml
  - source: salt://k8s/files/master/master-kubeconfig.yaml
  - template: jinja

set_kubelet:
  file.managed:
  - name: /etc/systemd/system/kubelet.service
  - source: salt://k8s/files/master/services/kubelet.service
  - template: jinja

create_manifest_dir:
  file.directory:
  - name: /etc/kubernetes/manifests
  - makedirs: true

set_kube-apiserver_pod:
  file.managed:
  - name: /etc/kubernetes/manifests/kube-apiserver.yaml
  - source: salt://k8s/files/master/manifests/kube-apiserver.yaml
  - template: jinja

set_kube-proxy_pod:
  file.managed:
  - name: /etc/kubernetes/manifests/kube-proxy.yaml
  - source: salt://k8s/files/master/manifests/kube-proxy.yaml
  - template: jinja

set_kube-controller-manager_pod:
  file.managed:
  - name: /etc/kubernetes/manifests/kube-controller-manager.yaml
  - source: salt://k8s/files/master/manifests/kube-controller-manager.yaml
  - template: jinja

set_kube-scheduler_pod:
  file.managed:
  - name: /etc/kubernetes/manifests/kube-scheduler.yaml
  - source: salt://k8s/files/master/manifests/kube-scheduler.yaml
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
  - source: salt://k8s/files/master/services/kubelet.service
  - template: jinja

wait_for_apiserver:
  http.wait_for_successful_query:
  - name: http://127.0.0.1:{{ pillar["k8s_cluster"]["apiserver"]["insecure-port"]}}
  - status: 200
  - wait_for: 600
  - failhard: True

python-pip:
  pkg.installed

upgrade_pip:
  pip.installed:
  - name: pip
  - upgrade: True

install_kubernetes_python_client:
  pip.installed:
    - name: kubernetes
    - require:
      - pkg: python-pip

kube-system_namespace:
  cmd.run:
  - name: >-
      curl -H "Content-Type: application/json" -XPOST -d'{"apiVersion":"v1","kind":"Namespace","metadata":{"name":"kube-system"}}' "http://127.0.0.1:{{ salt['pillar.get']('k8s_cluster:apiserver:insecure-port') }}/api/v1/namespaces"

admin_private_key:
  x509.private_key_managed:
  - name: /etc/kubernetes/ssl/admin-key.pem
  - bits: 4096

admin_public_key:
  x509.certificate_managed:
    - name: /etc/kubernetes/ssl/admin.pem
    - ca_server: {{ pillar['k8s_cluster']['ca']['minion']}}
    - signing_policy: admin
    - public_key: /etc/kubernetes/ssl/admin-key.pem
    - CN: kube-admin
    - days_remaining: 30
    - backup: True
    - require:
      - x509: get_ca_pem

install_python_k8s_client_dependecies:
  pip.installed:
  - name: urllib3 >= 1.20

install_python_k8s_client:
  pip.installed:
  - name: kubernetes

install_kubectl:
  file.managed:
  - name: /usr/bin/kubectl
  - source: https://dl.k8s.io/{{ salt['pillar.get']('k8s_cluster:k8s_version') }}/bin/linux/amd64/kubectl
  - source_hash: https://dl.k8s.io/{{ salt['pillar.get']('k8s_cluster:k8s_version') }}/bin/linux/amd64/kubectl.sha1
  - mode: '0755'

kubectl_config_file:
  file.managed:
  - name: /root/.kube/config
  - source: salt://k8s/files/master/kubectl/config
  - template: jinja
  - makedirs: true

deploy_flannel:
  file.managed:
  - name: /root/flannel.yaml
  - source: salt://k8s/files/flannel.yaml
  - template: jinja
  k8s_custom.yaml_applied:
  - name: flannel.yaml

clean_up_flannel_yaml:
  file.absent:
  - name: /root/flannel.yaml

deploy_kube-dns_yaml:
  file.managed:
  - name: /root/kube-dns.yaml
  - source: salt://k8s/files/kube-dns.yaml
  - template: jinja
  k8s_custom.yaml_applied:
  - name: kube-dns.yaml

clean_up_kube-dns_yaml:
  file.absent:
  - name: /root/kube-dns.yaml

set_k8s_role_grain:
  grains.present:
  - name: k8s_role
  - value: master

mine_update:
  module.run:
  - name: mine.update
