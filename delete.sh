#delivery
kubectl delete -f ./k8s/delivery/ingress.yaml
kubectl delete -f ./k8s/delivery/service.yaml
kubectl delete -f ./k8s/delivery/deployment.yaml
kubectl delete -f ./k8s/delivery/secrets.yaml
kubectl delete cm config-application-delivery
kubectl delete -f ./k8s/delivery/postgres/job.yaml
kubectl delete cm config-migration-properties-delivery
kubectl delete cm config-migration-changelog-delivery
helm delete postgresql-delivery

#warehouse
kubectl delete -f ./k8s/warehouse/ingress.yaml
kubectl delete -f ./k8s/warehouse/service.yaml
kubectl delete -f ./k8s/warehouse/deployment.yaml
kubectl delete -f ./k8s/warehouse/secrets.yaml
kubectl delete cm config-application-warehouse
kubectl delete -f ./k8s/warehouse/postgres/job.yaml
kubectl delete cm config-migration-properties-warehouse
kubectl delete cm config-migration-changelog-warehouse
helm delete postgresql-warehouse

#payment
kubectl delete -f ./k8s/payment/ingress.yaml
kubectl delete -f ./k8s/payment/service.yaml
kubectl delete -f ./k8s/payment/deployment.yaml
kubectl delete -f ./k8s/payment/secrets.yaml
kubectl delete cm config-application-payment
kubectl delete -f ./k8s/payment/postgres/job.yaml
kubectl delete cm config-migration-properties-payment
kubectl delete cm config-migration-changelog-payment
helm delete postgresql-payment

#order
kubectl delete -f ./k8s/order/ingress.yaml
kubectl delete -f ./k8s/order/service.yaml
kubectl delete -f ./k8s/order/deployment.yaml
kubectl delete -f ./k8s/order/secrets.yaml
kubectl delete cm config-application-order
kubectl delete -f ./k8s/order/postgres/job.yaml
kubectl delete cm config-migration-properties-order
kubectl delete cm config-migration-changelog-order
helm delete postgresql-order

helm delete nginx
kubectl delete -f ./k8s/namespaces.yaml