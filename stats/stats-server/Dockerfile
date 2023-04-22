FROM amazoncorretto:11
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/stats-server.jar"]

EXPOSE 9090