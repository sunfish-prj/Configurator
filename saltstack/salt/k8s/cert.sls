/etc/kubernetes/ssl:
  file.directory:
  - makedirs: True

{% set path = pillar['k8s_cluster']['ca']['folder'] + "/ca.pem" %}
{% set ca_minion = pillar['k8s_cluster']['ca']['minion'] %}
get_ca_pem:
  x509.pem_managed:
    - name: /etc/kubernetes/ssl/ca.pem
    - text: {{ salt['mine.get']( ca_minion, 'x509.get_pem_entries')[ ca_minion ][ path ]|replace('\n', '') }}
    - require:
      - file: /etc/kubernetes/ssl
