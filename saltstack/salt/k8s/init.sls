m2crypto_installed:
  pkg.installed:
  - name: python-m2crypto

install_kubelet_dependencies:
  pkg.installed:
  - pkgs:
    - socat
    - ebtables

install_kubelet:
  file.managed:
  - name: /usr/bin/kubelet
  - source: https://dl.k8s.io/{{ salt['pillar.get']('k8s_cluster:k8s_version') }}/bin/linux/amd64/kubelet
  - source_hash: https://dl.k8s.io/{{ salt['pillar.get']('k8s_cluster:k8s_version') }}/bin/linux/amd64/kubelet.sha1
  - mode: '0755'

{% set cni_version = salt['pillar.get']('k8s_cluster:CNI_version') %}
install_cni:
  archive.extracted:
  - name: /opt/cni/bin
  - source: https://github.com/containernetworking/cni/releases/download/{{ cni_version }}/cni-amd64-{{ cni_version }}.tgz
  - source_hash: https://github.com/containernetworking/cni/releases/download/{{ cni_version }}/cni-amd64-{{ cni_version }}.tgz.sha1

{% if grains['os'] == 'RedHat' %}
permissive_selinux:
  selinux.mode:
    name: permissive
{% endif %}
