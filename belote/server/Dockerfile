FROM eclipse-temurin:21_35-jre-ubi9-minimal

ENV LANGUAGE='en_US:en'

COPY --chown=185 target/*-runner.jar /deployments/quarkus.jar

EXPOSE 8080
USER 185
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

ENTRYPOINT ["java", "-jar", "/deployments/quarkus.jar"]