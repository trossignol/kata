default: run

run:
	quarkus run

docker-build:
	mvn clean package
	docker-compose build belote

docker-run:
	docker-compose run belote

docker-push:
	docker-compose push belote

deploy:
	export GCP_PROJECT_ID=belote-403119
	mvn clean package appengine:deploy -DskipTests