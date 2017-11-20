#NOT WORKING: REDO WITH ORCHESTRATION (jinja rendered before executing state )
copy_yaml_files:
  file.recurse:
  - name: /root/
  - source: salt://k8s_yaml
{% for yaml in salt.file.readdir("/root/") %}
deploy_{{yaml}}:
  k8s_custom.yaml_applied:
  - name: {{yaml.file}}
{% endfor %}
clean_up_yaml_files:
  file.absent:
  - name: /root/k8s_yaml
