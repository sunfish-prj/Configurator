add_repo_oracle_java:
  pkgrepo.managed:
  - ppa: webupd8team/java

accept_oracle_terms:
  debconf.set:
    - name: oracle-java8-installer
    - data:
        'shared/accepted-oracle-license-v1-1': {'type': 'boolean', 'value': True }

oracle-java8-installer:
  pkg:
    - installed
    - require:
      - pkgrepo: add_repo_oracle_java
      - debconf: accept_oracle_terms

add_salt_api_cert:
  cmd.run:
  - name: keytool -import -noprompt -trustcacerts -alias localhost -file /etc/pki/tls/certs/localhost.crt -keystore /usr/lib/jvm/java-8-oracle/jre/lib/security/cacerts -storepass changeit

create_configurator_key:
  cmd.run:
  - name: keytool -noprompt -genkeypair -alias configurator -keyalg RSA -keysize 4096 -keystore /usr/lib/jvm/java-8-oracle/jre/lib/security/cacerts -storepass changeit -validity 3650 -keypass password -dname "cn=Configurator, c=IT"

install_mongo:
  module.run:
  - name: state.apply
  - mods: mongodb
