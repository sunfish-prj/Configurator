remove_manifest:
  file.absent:
  - name: /etc/kubernetes/manifests

wait_for_removal:
  cmd.run:
  - name: sleep 5

stop_kubelet:
  service.dead:
  - name: kubelet
  - enable: false

stop_etcd:
  service.dead:
  - name: docker-etcd
  - enable: false

install_docker-py:
  pip.installed:
  - name: docker-py

remove_etcd_data:
  dockerng.volume_absent:
  - name: data.etcd
  - require:
    - pip: install_docker-py

remove_kubernetes_settings:
  file.absent:
  - name: /etc/kubernetes
