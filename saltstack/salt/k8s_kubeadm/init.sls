k8s_repo_installed:
  pkgrepo.managed:
  {% if grains['os'] == 'Ubuntu' %}
  - name: deb http://apt.kubernetes.io/ kubernetes-{{ salt.grains.get('oscodename') }} main
  - file: /etc/apt/sources.list.d/kubernetes.list
  - key_url: https://packages.cloud.google.com/apt/doc/apt-key.gpg
  {% elif grains['os'] == 'RedHat' %}
  - name: kubernetes
  - humanname: Kubernetes
  - baseurl: http://yum.kubernetes.io/repos/kubernetes-el7-x86_64
  - gpgcheck: 1
  - repo_gpgcheck: 1
  - gpgkey:
    - https://packages.cloud.google.com/yum/doc/yum-key.gpg
    - https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
  {% endif %}

k8s_pkgs_installed:
  pkg.installed:
  - pkgs:
    - kubelet
    - kubeadm
    - kubectl
    - kubernetes-cni
{% if grains['os'] == 'RedHat' %}
permissive_selinux:
  selinux.mode:
    name: permissive
kubelet_running:
  service.running:
  - name: kubelet
  - enable: true
{% endif %}
