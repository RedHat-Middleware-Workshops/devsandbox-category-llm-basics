# oc get deployment,route,service,secret,pvc -o name | grep lab- | xargs oc delete
oc get it,deployment,route,service,secret,pvc -o name | grep 'minio\|tf-server\|price' | xargs oc delete