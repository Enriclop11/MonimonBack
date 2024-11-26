FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8080
EXPOSE 48166/udp
EXPOSE 51282/udp
EXPOSE 59622/udp
ENTRYPOINT ["java","-jar","/app.jar"]