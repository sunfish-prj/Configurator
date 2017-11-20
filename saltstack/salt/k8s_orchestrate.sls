set_up_ca:
  salt.state:
  - tgt: 'saltmaster'
  - sls:
    - k8s.ca
  - failhard: True

prepare_nodes:
  salt.state:
  - tgt: '^(node|master)'
  - tgt_type: pcre
  - sls:
    - docker
    - k8s
  - failhard: True

set_master:
  salt.state:
  - tgt: 'master'
  - sls:
    - k8s.master
  - failhard: True

set_nodes:
  salt.state:
  - tgt: 'node*'
  - sls:
    - k8s.node
  - failhard: True
