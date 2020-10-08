# Alpine Linux with OpenJDK JRE
FROM tomcat:jdk11-adoptopenjdk-openj9


COPY . /api/app
WORKDIR /api/app
RUN apt update && apt install -y maven
RUN mvn clean install
RUN cp /api/app/target/warfilename.war /usr/local/tomcat/webapps/warfilename.war
RUN rm -rf /api/app
EXPOSE 8080
#CMD tail -f /dev/null
