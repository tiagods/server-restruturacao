#FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
#COPY pom.xml /build/
#COPY src /build/src/
#WORKDIR /build/
#RUN mvn package
#FROM openjdk:8-jre-alpine
#WORKDIR /app
#COPY --from=MAVEN_BUILD /build/target/server-consumer.jar /app/
#ENTRYPOINT ["java", "-jar", "server-consumer.jar"]
FROM openjdk:8-jre-alpine
#VOLUME /tmp
ADD target/*.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]