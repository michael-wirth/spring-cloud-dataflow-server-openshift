#!/usr/bin/env bash

eval $(minishift oc-env)
oc project scdf
#oc delete all,secret,configmap,pv,pvc --selector template=scdf-kafka
oc delete all,secret,configmap,pvc --selector template=scdf-kafka
oc replace --force -f scdf-with-kafka-service.yml
