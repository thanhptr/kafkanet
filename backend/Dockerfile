FROM openjdk
COPY target/backend.jar /opt/backend.jar
ENTRYPOINT ["java","-Xmx256m", "-Xss512m", "-jar","/opt/backend.jar"]
EXPOSE 2035
