# The Dockerfile is broken into three steps.

# 1. Download the dependencies including Carnival, and prepares build environment
# - Full build environment
# - Uses: GitHub credentials, project config files. Does not contain source code.
# - Note: This is the image used for running tests

# 2. Build the Jar
# - Continues from image (1)
# - Uses: Cached dependencies from (1), copies in app source

# 3. Run the app
# - Stripped down environment with a JVM
# - Uses: JAR from (2)


ARG GRADLE_VERSION=7.2.0-jdk11
ARG JAVA_OPTS

#####
# Stage 1: Downloads and caches dependencies including Carnival, and prepares build environment
#####
FROM gradle:${GRADLE_VERSION} as base

ARG GITHUB_USER
ARG GITHUB_TOKEN

ENV GRADLE_OPTS ${JAVA_OPTS}
ENV GRADLE_USER_HOME /home/gradle


ENV APP_SRC /opt/carnival-micronaut
RUN mkdir ${APP_SRC}

COPY build.gradle      ${APP_SRC}
COPY gradle.properties ${APP_SRC}
COPY settings.gradle   ${APP_SRC}

WORKDIR ${APP_SRC}

# Only download dependencies
# Eat the expected build failure since no source code has been copied yet
RUN gradle clean buildDependents --no-daemon --console=plain || true


# prepare source build environment
ENV CARNIVAL_MICRONAUT      /opt/carnival-micronaut
ENV CARNIVAL_MICRONAUT_HOME /opt/carnival-micronaut-home
ENV APOC_HOME               /opt/neo4j/plugins
ENV APOC_VERSION            3.4.0.7

RUN mkdir ${CARNIVAL_MICRONAUT_HOME}
COPY ./carnival-micronaut-home ${CARNIVAL_MICRONAUT_HOME}/.

COPY micronaut-cli.yml ${CARNIVAL_MICRONAUT}/.

COPY data/survey/observations_survey.csv ${CARNIVAL_MICRONAUT}/data/survey/observations_survey.csv

WORKDIR ${CARNIVAL_MICRONAUT}


#####
# Stage 2: Builds The application as a Jar
#####
FROM base AS builder

ENV GRADLE_OPTS ${JAVA_OPTS}

COPY ./src ${CARNIVAL_MICRONAUT}/src

#RUN gradle shadowJar -i --stacktrace
RUN gradle shadowJar --no-daemon

#####
# Stage 3: Runs The App
#####
FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim AS app

ENV CARNIVAL_MICRONAUT      /opt/carnival-micronaut
ENV CARNIVAL_MICRONAUT_HOME /opt/carnival-micronaut-home

WORKDIR ${CARNIVAL_MICRONAUT}

COPY --from=builder ${CARNIVAL_MICRONAUT_HOME} ${CARNIVAL_MICRONAUT_HOME}/.

COPY --from=builder ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut-0.1-all.jar \
                    ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut.jar

COPY --from=builder ${CARNIVAL_MICRONAUT}/data/survey/observations_survey.csv \
                    ${CARNIVAL_MICRONAUT}/data/survey/observations_survey.csv

CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} \
      -Dcarnival.home=${CARNIVAL_MICRONAUT_HOME} \
      -Dlogback.configurationFile=${CARNIVAL_MICRONAUT_HOME}/config/logback.xml \
      -Dmicronaut.config.files=${CARNIVAL_MICRONAUT_HOME}/config/application.yml \
      -jar ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut.jar
