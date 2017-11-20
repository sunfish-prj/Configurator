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

remove_kubernetes_settings:
  file.absent:
  - name: /etc/kubernetes

restore_kubernetes_folder:
  file.directory:
  - name: /etc/kubernetes
