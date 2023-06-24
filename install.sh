kubectl apply -f ./k8s/namespaces.yaml
kubectl config set-context --current --namespace=otus-ms-hw-transactions
helm install nginx ingress-nginx/ingress-nginx -f ./k8s/nginx/nginx-ingress.yaml

sleep 30

#delivery
helm install postgresql-delivery bitnami/postgresql -f ./k8s/delivery/postgres/values.yaml
kubectl create cm config-migration-changelog-delivery --from-file=./k8s/delivery/postgres/changelog.yaml
kubectl create cm config-migration-properties-delivery --from-file=./k8s/delivery/postgres/liquibase.properties

kubectl apply -f ./k8s/delivery/postgres/job.yaml

kubectl create cm config-application-delivery --from-file=./k8s/delivery/application.yaml
kubectl apply -f ./k8s/delivery/secrets.yaml

kubectl apply -f ./k8s/delivery/deployment.yaml
kubectl apply -f ./k8s/delivery/service.yaml
kubectl apply -f ./k8s/delivery/ingress.yaml

#warehouse
helm install postgresql-warehouse bitnami/postgresql -f ./k8s/warehouse/postgres/values.yaml
kubectl create cm config-migration-changelog-warehouse --from-file=./k8s/warehouse/postgres/changelog.yaml
kubectl create cm config-migration-properties-warehouse --from-file=./k8s/warehouse/postgres/liquibase.properties

kubectl apply -f ./k8s/warehouse/postgres/job.yaml

kubectl create cm config-application-warehouse --from-file=./k8s/warehouse/application.yaml
kubectl apply -f ./k8s/warehouse/secrets.yaml

kubectl apply -f ./k8s/warehouse/deployment.yaml
kubectl apply -f ./k8s/warehouse/service.yaml
kubectl apply -f ./k8s/warehouse/ingress.yaml

#payment
helm install postgresql-payment bitnami/postgresql -f ./k8s/payment/postgres/values.yaml
kubectl create cm config-migration-changelog-payment --from-file=./k8s/payment/postgres/changelog.yaml
kubectl create cm config-migration-properties-payment --from-file=./k8s/payment/postgres/liquibase.properties

kubectl apply -f ./k8s/payment/postgres/job.yaml

kubectl create cm config-application-payment --from-file=./k8s/payment/application.yaml
kubectl apply -f ./k8s/payment/secrets.yaml

kubectl apply -f ./k8s/payment/deployment.yaml
kubectl apply -f ./k8s/payment/service.yaml
kubectl apply -f ./k8s/payment/ingress.yaml

#order
helm install postgresql-order bitnami/postgresql -f ./k8s/order/postgres/values.yaml
kubectl create cm config-migration-changelog-order --from-file=./k8s/order/postgres/changelog.yaml
kubectl create cm config-migration-properties-order --from-file=./k8s/order/postgres/liquibase.properties

kubectl apply -f ./k8s/order/postgres/job.yaml

kubectl create cm config-application-order --from-file=./k8s/order/application.yaml
kubectl apply -f ./k8s/order/secrets.yaml

kubectl apply -f ./k8s/order/deployment.yaml
kubectl apply -f ./k8s/order/service.yaml
kubectl apply -f ./k8s/order/ingress.yaml

#billing
helm install postgresql-billing bitnami/postgresql -f ./k8s/billing/postgres/values.yaml
kubectl create cm config-migration-changelog-billing --from-file=./k8s/billing/postgres/changelog.yaml
kubectl create cm config-migration-properties-billing --from-file=./k8s/billing/postgres/liquibase.properties

kubectl apply -f ./k8s/billing/postgres/job.yaml

kubectl create cm config-application-billing --from-file=./k8s/billing/application.yaml
kubectl apply -f ./k8s/billing/secrets.yaml

kubectl apply -f ./k8s/billing/deployment.yaml
kubectl apply -f ./k8s/billing/service.yaml
kubectl apply -f ./k8s/billing/ingress.yaml

#user
helm install postgresql-user bitnami/postgresql -f ./k8s/user/postgres/values.yaml
kubectl create cm config-migration-changelog-user --from-file=./k8s/user/postgres/changelog.yaml
kubectl create cm config-migration-properties-user --from-file=./k8s/user/postgres/liquibase.properties

kubectl apply -f ./k8s/user/postgres/job.yaml

kubectl create cm config-application-user --from-file=./k8s/user/application.yaml
kubectl apply -f ./k8s/user/secrets.yaml

kubectl apply -f ./k8s/user/deployment.yaml
kubectl apply -f ./k8s/user/service.yaml
kubectl apply -f ./k8s/user/ingress.yaml

#kafka and debezium
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.20.0/install.sh | bash -s v0.20.0
kubectl create -f https://operatorhub.io/install/strimzi-kafka-operator.yaml
kubectl apply -f ./k8s/debezium/kafka.yaml
kubectl wait kafka/debezium-cluster --for=condition=Ready --timeout=300s

kubectl apply -f ./k8s/debezium/connector.yaml

sleep 30

kubectl apply -f ./k8s/debezium/pg-connector.yaml

#notification
helm install postgresql-notification bitnami/postgresql -f ./k8s/notification/postgres/values.yaml
kubectl create cm config-migration-changelog-notification --from-file=./k8s/notification/postgres/changelog.yaml
kubectl create cm config-migration-properties-notification --from-file=./k8s/notification/postgres/liquibase.properties

kubectl apply -f ./k8s/notification/postgres/job.yaml

kubectl create cm config-application-notification --from-file=./k8s/notification/application.yaml
kubectl apply -f ./k8s/notification/secrets.yaml

kubectl apply -f ./k8s/notification/deployment.yaml
kubectl apply -f ./k8s/notification/service.yaml
kubectl apply -f ./k8s/notification/ingress.yaml

sleep 30