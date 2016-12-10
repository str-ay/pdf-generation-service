FROM openjdk:8-jre-alpine
MAINTAINER Aleksandr Streltsov <jness.pro@gmail.com>
LABEL Description="PDF generation service. Builds PDF from ODT template (Freemarker) and JSON data" Vendor="Aleksandr Streltsov" Version="1.0"

EXPOSE 8080
VOLUME /tmp /var/log /var/pdf_tasks
ADD pdf-generation-service.jar app.jar
ADD fonts/* /usr/share/fonts/
#RUN apt-get update && apt-get upgrade -y
#RUN apt-get install apt-transport-https -y
#RUN mkdir /var/lib/update-notifier/package-data-downloads/partial
#RUN chown _apt:root /var/lib/update-notifier/package-data-downloads/partial
#RUN echo ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true | debconf-set-selections
#RUN apt-get install -y msttcorefonts
#        apt-get install -y ttf-mscorefonts-installer
#RUN apt-get install -y software-properties-common fontconfig && \
#    add-apt-repository ppa:webupd8team/java -y && \
#    apt-get update && \
#    echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
#    apt-get install -y oracle-java8-installer && \
#    apt-get clean
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]