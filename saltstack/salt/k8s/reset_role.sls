{% if grains['k8s_role'] == 'master' %}
include:
- k8s.master.reset_master
{% elif grains['k8s_role'] == 'node'%}
include:
- k8s.node.reset_node
{% endif %}

remove_k8s_grains:
  grains.absent:
  - name: k8s_role
  - destructive: true
  module.run:
  - name: mine.update
