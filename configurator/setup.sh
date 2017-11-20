#!/bin/bash

salt 'saltmaster' state.apply salt_rest_api
salt 'saltmaster' state.apply configurator
apt-get --assume-yes install maven
mvn -f /root/configurator/pom.xml clean package
