modify_saltmaster_config:
  file.append:
  - name: /etc/salt/master
  - text: |
      external_auth:
        pam:
          saltuser:
            - .*
            - '@wheel'
            - '@runner'
            - '@jobs'

      rest_cherrypy:
        host: 127.0.0.1
        port: 8000
        ssl_crt: /etc/pki/tls/certs/localhost.crt
        ssl_key: /etc/pki/tls/certs/localhost.key

add_user_saltclient:
  user.present:
  - name: saltuser
  - password: saltuser
  - hash_password: true

deny_saltuser_ssh:
  file.append:
  - name: /etc/ssh/sshd_config
  - text: "DenyUsers saltuser"

install_salt-api:
  pkg.installed:
  - name: salt-api

install_pip:
  pkg.installed:
  - name: python-pip

install_cherrypy:
  pip.installed:
  - name: cherrypy == 3.2.3
  - require:
    - pkg: python-pip

create_self_signed_certificate:
  module.run:
  - name: tls.create_self_signed_cert

restart_salt-master:
  module.run:
  - name: service.restart
  - m_name: salt-master

restart_salt-api:
  module.run:
  - name: service.restart
  - m_name: salt-api
