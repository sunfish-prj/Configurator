m2crypto_installed:
  pkg.installed:
  - name: python-m2crypto

salt-minion:
  service.running:
    - enable: True
    - listen:
      - file: /etc/salt/minion.d/signing_policies.conf

/etc/salt/minion.d/signing_policies.conf:
  file.managed:
    - source: salt://k8s/files/ca/signing_policies.conf
    - template: jinja

ca_folder:
  file.directory:
  - name: {{ pillar['k8s_cluster']['ca']['folder']  }}

private_key:
  x509.private_key_managed:
  - name: {{ pillar['k8s_cluster']['ca']['folder'] }}/ca-key.pem
  - bits: 4096

{{ pillar['k8s_cluster']['ca']['folder'] }}/ca.pem:
  x509.certificate_managed:
    - signing_private_key: {{ pillar['k8s_cluster']['ca']['folder'] }}/ca-key.pem
    - CN: kube-ca
    - basicConstraints: "critical CA:true"
    - keyUsage: "critical cRLSign, keyCertSign"
    - subjectKeyIdentifier: hash
    - authorityKeyIdentifier: keyid,issuer:always
    - days_valid: 10000
    - require:
      - file: ca_folder
      - pkg: m2crypto_installed

mine.send:
  module.run:
    - func: x509.get_pem_entries
    - kwargs:
        glob_path: {{ pillar['k8s_cluster']['ca']['folder']}}/ca.pem
    - onchanges:
      - x509: {{ pillar['k8s_cluster']['ca']['folder'] }}/ca.pem
