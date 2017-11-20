{% for yaml in pillar['k8s_cluster']['deployments'] %}
deploy_{{ yaml.file }}:
  file.managed:
  - name: /root/{{yaml.file}}
  - source: salt://k8s_yaml/{{yaml.file}}
  k8s_custom.yaml_applied:
  - name: {{yaml.file}}
clean_up_{{ yaml.file }}_file:
  file.absent:
  - name: /root/{{yaml.file}}
{% endfor %}
