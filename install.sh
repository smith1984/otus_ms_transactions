kubectl apply -f ./k8s/namespaces.yaml
kubectl config set-context --current --namespace=otus-ms-hw-transactions

#olm
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.20.0/install.sh | bash -s v0.20.0

#ingress, monitoring, logging
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx/
helm repo add opensearch https://opensearch-project.github.io/helm-charts/
helm repo add fluent https://fluent.github.io/helm-charts
helm repo update

helm install nginx ingress-nginx/ingress-nginx -f ./k8s/nginx/nginx-ingress.yaml
helm install prometheus prometheus-community/kube-prometheus-stack -f ./k8s/monitoring/values.yaml --atomic
helm install opensearch opensearch/opensearch
helm install opensearch-dashboards opensearch/opensearch-dashboards -f ./k8s/logging/values.yaml

#fluent-bit
kubectl create cm config-fluent-bit --from-file=./k8s/logging/fluent-bit.conf
kubectl create cm config-parser-fluent-bit --from-file=./k8s/logging/parsers.conf
kubectl apply -f ./k8s/logging/deployment.yaml
kubectl apply -f ./k8s/logging/service.yaml


#kafka
kubectl create -f https://operatorhub.io/install/strimzi-kafka-operator.yaml
kubectl apply -f ./k8s/debezium/kafka.yaml
kubectl wait kafka/debezium-cluster --for=condition=Ready --timeout=300s

#delivery
helm install postgresql-delivery bitnami/postgresql -f ./k8s/delivery/postgres/values.yaml
kubectl create cm config-migration-changelog-delivery --from-file=./k8s/delivery/postgres/changelog.yaml
kubectl create cm config-migration-properties-delivery --from-file=./k8s/delivery/postgres/liquibase.properties
kubectl apply -f ./k8s/delivery/postgres/job.yaml
kubectl create cm config-application-delivery --from-file=./k8s/delivery/application.yaml
kubectl create cm config-logback-delivery --from-file=./k8s/delivery/logback.xml
kubectl apply -f ./k8s/delivery/secrets.yaml
kubectl apply -f ./k8s/delivery/deployment.yaml
kubectl apply -f ./k8s/delivery/service.yaml
kubectl apply -f ./k8s/delivery/ingress.yaml
kubectl apply -f ./k8s/delivery/service-monitor.yaml

#warehouse
helm install postgresql-warehouse bitnami/postgresql -f ./k8s/warehouse/postgres/values.yaml
kubectl create cm config-migration-changelog-warehouse --from-file=./k8s/warehouse/postgres/changelog.yaml
kubectl create cm config-migration-properties-warehouse --from-file=./k8s/warehouse/postgres/liquibase.properties
kubectl apply -f ./k8s/warehouse/postgres/job.yaml
kubectl create cm config-application-warehouse --from-file=./k8s/warehouse/application.yaml
kubectl create cm config-logback-warehouse --from-file=./k8s/warehouse/logback.xml
kubectl apply -f ./k8s/warehouse/secrets.yaml
kubectl apply -f ./k8s/warehouse/deployment.yaml
kubectl apply -f ./k8s/warehouse/service.yaml
kubectl apply -f ./k8s/warehouse/ingress.yaml
kubectl apply -f ./k8s/warehouse/service-monitor.yaml

#payment
helm install postgresql-payment bitnami/postgresql -f ./k8s/payment/postgres/values.yaml
kubectl create cm config-migration-changelog-payment --from-file=./k8s/payment/postgres/changelog.yaml
kubectl create cm config-migration-properties-payment --from-file=./k8s/payment/postgres/liquibase.properties
kubectl apply -f ./k8s/payment/postgres/job.yaml
kubectl create cm config-application-payment --from-file=./k8s/payment/application.yaml
kubectl create cm config-logback-payment --from-file=./k8s/payment/logback.xml
kubectl apply -f ./k8s/payment/secrets.yaml
kubectl apply -f ./k8s/payment/deployment.yaml
kubectl apply -f ./k8s/payment/service.yaml
kubectl apply -f ./k8s/payment/ingress.yaml
kubectl apply -f ./k8s/payment/service-monitor.yaml

#order
helm install postgresql-order bitnami/postgresql -f ./k8s/order/postgres/values.yaml
kubectl create cm config-migration-changelog-order --from-file=./k8s/order/postgres/changelog.yaml
kubectl create cm config-migration-properties-order --from-file=./k8s/order/postgres/liquibase.properties
kubectl apply -f ./k8s/order/postgres/job.yaml
kubectl create cm config-application-order --from-file=./k8s/order/application.yaml
kubectl create cm config-logback-order --from-file=./k8s/order/logback.xml
kubectl apply -f ./k8s/order/secrets.yaml
kubectl apply -f ./k8s/order/deployment.yaml
kubectl apply -f ./k8s/order/service.yaml
kubectl apply -f ./k8s/order/ingress.yaml
kubectl apply -f ./k8s/order/service-monitor.yaml

#billing
helm install postgresql-billing bitnami/postgresql -f ./k8s/billing/postgres/values.yaml
kubectl create cm config-migration-changelog-billing --from-file=./k8s/billing/postgres/changelog.yaml
kubectl create cm config-migration-properties-billing --from-file=./k8s/billing/postgres/liquibase.properties
kubectl apply -f ./k8s/billing/postgres/job.yaml
kubectl create cm config-application-billing --from-file=./k8s/billing/application.yaml
kubectl create cm config-logback-billing --from-file=./k8s/billing/logback.xml
kubectl apply -f ./k8s/billing/secrets.yaml
kubectl apply -f ./k8s/billing/deployment.yaml
kubectl apply -f ./k8s/billing/service.yaml
kubectl apply -f ./k8s/billing/ingress.yaml
kubectl apply -f ./k8s/billing/service-monitor.yaml

#user
helm install postgresql-user bitnami/postgresql -f ./k8s/user/postgres/values.yaml
kubectl create cm config-migration-changelog-user --from-file=./k8s/user/postgres/changelog.yaml
kubectl create cm config-migration-properties-user --from-file=./k8s/user/postgres/liquibase.properties
kubectl apply -f ./k8s/user/postgres/job.yaml
kubectl create cm config-application-user --from-file=./k8s/user/application.yaml
kubectl create cm config-logback-user --from-file=./k8s/user/logback.xml
kubectl apply -f ./k8s/user/secrets.yaml
kubectl apply -f ./k8s/user/deployment.yaml
kubectl apply -f ./k8s/user/service.yaml
kubectl apply -f ./k8s/user/ingress.yaml
kubectl apply -f ./k8s/user/service-monitor.yaml

#debezium
kubectl apply -f ./k8s/debezium/connector.yaml
kubectl apply -f ./k8s/debezium/pg-connector.yaml

#notification
helm install postgresql-notification bitnami/postgresql -f ./k8s/notification/postgres/values.yaml
kubectl create cm config-migration-changelog-notification --from-file=./k8s/notification/postgres/changelog.yaml
kubectl create cm config-migration-properties-notification --from-file=./k8s/notification/postgres/liquibase.properties
kubectl apply -f ./k8s/notification/postgres/job.yaml
kubectl create cm config-application-notification --from-file=./k8s/notification/application.yaml
kubectl create cm config-logback-notification --from-file=./k8s/notification/logback.xml
kubectl apply -f ./k8s/notification/secrets.yaml
kubectl apply -f ./k8s/notification/deployment.yaml
kubectl apply -f ./k8s/notification/service.yaml
kubectl apply -f ./k8s/notification/ingress.yaml
kubectl apply -f ./k8s/notification/service-monitor.yaml