FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim

WORKDIR carnival-micronaut
COPY build/libs/carnival-micronaut-*-all.jar carnival-micronaut.jar

# set up home/data dir
WORKDIR home/config
COPY build/home/config/* .

WORKDIR ../data/source
COPY build/home/data/source/* .

WORKDIR ../graph
#COPY build/home/data/graph/* .

WORKDIR ../cache
#COPY build/home/data/cache/* .

WORKDIR ../../log
WORKDIR ../../

ENV NCCC_BIOBANK_SERVER_HOME ./home
ENV JAVA_OPTS '-Xms1G -Xmx2G -Dfile.encoding=UTF-8'

#CMD ["/bin/ls", "-R"]
#CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar carnival-micronaut.jar
EXPOSE 5858
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -Dcarnival.home=home -Dlogback.configurationFile=home/config/logback.xml -Dmicronaut.config.files=home/config/application.yml  -jar carnival-micronaut.jar