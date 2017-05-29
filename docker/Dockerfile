FROM openjdk:8-jre-alpine
MAINTAINER Aleksandr Streltsov <jness.pro@gmail.com>
LABEL Description="PDF generation service. Builds PDF from ODT template (Freemarker) and JSON data" Vendor="Aleksandr Streltsov" Version="1.0"

EXPOSE 8080
VOLUME /tmp /var/log /var/pdf_tasks
ADD pdf-generation-service.war app.war
RUN apk update
RUN apk upgrade
RUN apk add ttf-dejavu
ADD fonts/* /usr/share/fonts/
RUN sh -c 'touch /app.war'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.war"]