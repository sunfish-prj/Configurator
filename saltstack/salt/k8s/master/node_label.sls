{% for node in pillar['k8s_cluster']['node_labels'] %}
{% if salt.mine.get(node.node, 'k8s_role')[node.node] is defined %}
add_label_to_{{node.node}}:
  k8s_custom.label_node_present:
  - name: {{node.key}}
  - value: {{node.value}}
  - node: {{node.node}}
{% endif %}
{% endfor %}
