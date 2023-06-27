#delivery
kubectl delete -f ./k8s/delivery/service-monitor.yaml
kubectl delete -f ./k8s/delivery/ingress.yaml
kubectl delete -f ./k8s/delivery/service.yaml
kubectl delete -f ./k8s/delivery/deployment.yaml
kubectl delete -f ./k8s/delivery/secrets.yaml
kubectl delete cm config-application-delivery
kubectl delete cm config-logback-delivery
kubectl delete -f ./k8s/delivery/postgres/job.yaml
kubectl delete cm config-migration-properties-delivery
kubectl delete cm config-migration-changelog-delivery
helm delete postgresql-delivery

#warehouse
kubectl delete -f ./k8s/warehouse/service-monitor.yaml
kubectl delete -f ./k8s/warehouse/ingress.yaml
kubectl delete -f ./k8s/warehouse/service.yaml
kubectl delete -f ./k8s/warehouse/deployment.yaml
kubectl delete -f ./k8s/warehouse/secrets.yaml
kubectl delete cm config-application-warehouse
kubectl delete cm config-logback-warehouse
kubectl delete -f ./k8s/warehouse/postgres/job.yaml
kubectl delete cm config-migration-properties-warehouse
kubectl delete cm config-migration-changelog-warehouse
helm delete postgresql-warehouse

#payment
kubectl delete -f ./k8s/payment/service-monitor.yaml
kubectl delete -f ./k8s/payment/ingress.yaml
kubectl delete -f ./k8s/payment/service.yaml
kubectl delete -f ./k8s/payment/deployment.yaml
kubectl delete -f ./k8s/payment/secrets.yaml
kubectl delete cm config-application-payment
kubectl delete cm config-logback-payment
kubectl delete -f ./k8s/payment/postgres/job.yaml
kubectl delete cm config-migration-properties-payment
kubectl delete cm config-migration-changelog-payment
helm delete postgresql-payment

#order
kubectl delete -f ./k8s/order/service-monitor.yaml
kubectl delete -f ./k8s/order/ingress.yaml
kubectl delete -f ./k8s/order/service.yaml
kubectl delete -f ./k8s/order/deployment.yaml
kubectl delete -f ./k8s/order/secrets.yaml
kubectl delete cm config-application-order
kubectl delete cm config-logback-order
kubectl delete -f ./k8s/order/postgres/job.yaml
kubectl delete cm config-migration-properties-order
kubectl delete cm config-migration-changelog-order
helm delete postgresql-order

#billing
kubectl delete -f ./k8s/billing/service-monitor.yaml
kubectl delete -f ./k8s/billing/ingress.yaml
kubectl delete -f ./k8s/billing/service.yaml
kubectl delete -f ./k8s/billing/deployment.yaml
kubectl delete -f ./k8s/billing/secrets.yaml
kubectl delete cm config-application-billing
kubectl delete cm config-logback-billing
kubectl delete -f ./k8s/billing/postgres/job.yaml
kubectl delete cm config-migration-properties-billing
kubectl delete cm config-migration-changelog-billing
helm delete postgresql-billing

#user
kubectl delete -f ./k8s/user/service-monitor.yaml
kubectl delete -f ./k8s/user/ingress.yaml
kubectl delete -f ./k8s/user/service.yaml
kubectl delete -f ./k8s/user/deployment.yaml
kubectl delete -f ./k8s/user/secrets.yaml
kubectl delete cm config-application-user
kubectl delete cm config-logback-user
kubectl delete -f ./k8s/user/postgres/job.yaml
kubectl delete cm config-migration-properties-user
kubectl delete cm config-migration-changelog-user
helm delete postgresql-user

#notification
kubectl delete -f ./k8s/notification/service-monitor.yaml
kubectl delete -f ./k8s/notification/ingress.yaml
kubectl delete -f ./k8s/notification/service.yaml
kubectl delete -f ./k8s/notification/deployment.yaml
kubectl delete -f ./k8s/notification/secrets.yaml
kubectl delete cm config-application-notification
kubectl delete cm config-logback-notification
kubectl delete -f ./k8s/notification/postgres/job.yaml
kubectl delete cm config-migration-properties-notification
kubectl delete cm config-migration-changelog-notification
helm delete postgresql-notification

#kafka and debezium
kubectl delete -f ./k8s/debezium/pg-connector.yaml
kubectl delete -f ./k8s/debezium/connector.yaml
kubectl delete -f ./k8s/debezium/kafka.yaml
kubectl delete -f https://operatorhub.io/install/strimzi-kafka-operator.yaml

kubectl delete -f ./k8s/logging/service.yaml
kubectl delete -f ./k8s/logging/deployment.yaml
kubectl delete cm config-parser-fluent-bit
kubectl delete cm config-fluent-bit

helm delete prometheus
helm delete opensearch
helm delete opensearch-dashboards
helm delete nginx

kubectl delete -f ./k8s/namespaces.yaml