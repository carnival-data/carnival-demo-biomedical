ARG  GRADLE_VERSION=jdk11
FROM gradle:${GRADLE_VERSION} AS builder

# Install linux utils
RUN apt-get update && \
#    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends \
      dos2unix \
      rename   \
      sed      \
      git      \
      &&       \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

### Development Image

ENV CARNIVAL_MICRONAUT      /opt/carnival-micronaut
ENV CARNIVAL_MICRONAUT_HOME /opt/carnival-micronaut-home
ENV APOC_HOME               /opt/neo4j/plugins
ENV APOC_VERSION            3.4.0.7
ENV CARNIVAL_LIB_SRC        /opt/carnival
ENV GRADLE_HOME             /opt/gradle

RUN mkdir ${CARNIVAL_MICRONAUT}

COPY ./carnival-micronaut-home ${CARNIVAL_MICRONAUT_HOME}/.

COPY gradle.properties ${CARNIVAL_MICRONAUT}/.
COPY build.gradle      ${CARNIVAL_MICRONAUT}/.
COPY settings.gradle   ${CARNIVAL_MICRONAUT}/.
COPY micronaut-cli.yml ${CARNIVAL_MICRONAUT}/.

COPY ./src ${CARNIVAL_MICRONAUT}/src

ARG GITHUB_USER
ARG GITHUB_TOKEN

ARG JAVA_OPTS

WORKDIR ${CARNIVAL_MICRONAUT}

RUN gradle shadowJar

#CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} \
#-Dcarnival.home=${CARNIVAL_MICRONAUT_HOME} \
#-Dlogback.configurationFile=${CARNIVAL_MICRONAUT_HOME}/config/logback.xml \
#-Dmicronaut.config.files=${CARNIVAL_MICRONAUT_HOME}/config/application.yml \
#-jar ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut-0.1-all.jar

FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim AS app

ENV CARNIVAL_MICRONAUT      /opt/carnival-micronaut
ENV CARNIVAL_MICRONAUT_HOME /opt/carnival-micronaut-home

COPY --from=builder ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut-0.1-all.jar ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut.jar

COPY ./carnival-micronaut-home ${CARNIVAL_MICRONAUT_HOME}/.

COPY gradle.properties ${CARNIVAL_MICRONAUT}/.
COPY build.gradle      ${CARNIVAL_MICRONAUT}/.
COPY settings.gradle   ${CARNIVAL_MICRONAUT}/.
COPY micronaut-cli.yml ${CARNIVAL_MICRONAUT}/.

COPY ./src ${CARNIVAL_MICRONAUT}/src

CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} \
      -Dcarnival.home=${CARNIVAL_MICRONAUT_HOME} \
      -Dlogback.configurationFile=${CARNIVAL_MICRONAUT_HOME}/config/logback.xml \
      -Dmicronaut.config.files=${CARNIVAL_MICRONAUT_HOME}/config/application.yml \
      -jar ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut.jar
