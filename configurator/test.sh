#!/bin/bash

curl -ik https://localhost:8443/api/configurator/v1/vms -H "Content-Type: application/json" -X POST -d '{"vmID":5,"vmType":"fresh","confID":"1"}'
curl -ik https://localhost:8443/api/configurator/v1/vms/1 -H "Content-Type: application/json" -X PUT -d '{"vmID":1,"vmType":"containerized","confID":"1"}'
curl -ik https://localhost:8443/api/configurator/v1/vms/2 -H "Content-Type: application/json" -X PUT -d '{"vmID":2,"vmType":"containerized","confID":"1"}'
curl -ik https://localhost:8443/api/configurator/v1/nodes -H "Content-Type: application/json" -X POST -d '{"vmID":1,"nodeID":1,"label":[]}'
curl -ik https://localhost:8443/api/configurator/v1/nodes -H "Content-Type: application/json" -X POST -d '{"vmID":2,"nodeID":2,"label":[]}'
curl -ik https://localhost:8443/api/configurator/v1/nodes/1 -H "Content-Type: application/json" -X DELETE
