# The Dockerfile is broken into three steps.

# 1. Download the dependencies, including Carnival
# - Full build environment
# - Uses: GitHub credentials, project config files

# 2. Build the app
# - Full build environment
# - Uses: Cached dependencies from (1), app source and config

# 3. Run the app
# - Stripped down environment with a JVM
# - Uses: JAR from (2)

ARG GRADLE_VERSION=jdk11

# Stage 1: Downloads and caches dependencies, including Carnival
FROM gradle:${GRADLE_VERSION} as cache

ARG GITHUB_USER
ARG GITHUB_TOKEN

ARG JAVA_OPTS

ENV GRADLE_OPTS ${JAVA_OPTS}

ENV GRADLE_USER_HOME /home/gradle

COPY build.gradle      ${GRADLE_USER_HOME}
COPY gradle.properties ${GRADLE_USER_HOME}
COPY settings.gradle   ${GRADLE_USER_HOME}

WORKDIR ${GRADLE_USER_HOME}

RUN echo && \
    echo Proceding to Download Dependencies && \
    # Will show downloads occurring
    # gradle dependencies -i --stacktrace
    gradle dependencies 

# Message when downloading dependencies. Fix?
# Cache entries evicted. In-memory cache of /home/gradle/.gradle/checksums/sha1-checksums.bin: Size{400} MaxSize{400}, CacheStats{hitCount=241, missCount=561, loadSuccessCount=561, loadExceptionCount=0, totalLoadTime=78824764, evictionCount=161} 
# Performance may suffer from in-memory cache misses. Increase max heap size of Gradle build process to reduce cache misses.

# Stage 2: Builds The Application
FROM gradle:${GRADLE_VERSION} AS builder

ARG GITHUB_USER
ARG GITHUB_TOKEN

ARG JAVA_OPTS

ENV GRADLE_OPTS ${JAVA_OPTS}

ENV CARNIVAL_MICRONAUT      /opt/carnival-micronaut
ENV CARNIVAL_MICRONAUT_HOME /opt/carnival-micronaut-home
ENV APOC_HOME               /opt/neo4j/plugins
ENV APOC_VERSION            3.4.0.7
#ENV CARNIVAL_LIB_SRC        /opt/carnival
ENV GRADLE_USER_HOME        /home/gradle

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends \
      dos2unix \
      git      \
      rename   \
      sed      \
      &&       \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Bring in cached dependencies
COPY --from=cache ${GRADLE_USER_HOME}/caches ${GRADLE_USER_HOME}/caches

RUN mkdir ${CARNIVAL_MICRONAUT}

COPY ./carnival-micronaut-home ${CARNIVAL_MICRONAUT_HOME}/.

COPY gradle.properties ${CARNIVAL_MICRONAUT}/.
COPY build.gradle      ${CARNIVAL_MICRONAUT}/.
COPY settings.gradle   ${CARNIVAL_MICRONAUT}/.
COPY micronaut-cli.yml ${CARNIVAL_MICRONAUT}/.

COPY ./src ${CARNIVAL_MICRONAUT}/src

WORKDIR ${CARNIVAL_MICRONAUT}

#RUN gradle shadowJar -i --stacktrace
RUN gradle shadowJar

# Stage 3: Runs The App
FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim AS app

ENV CARNIVAL_MICRONAUT      /opt/carnival-micronaut
ENV CARNIVAL_MICRONAUT_HOME /opt/carnival-micronaut-home

ARG JAVA_OPTS

COPY --from=builder ${CARNIVAL_MICRONAUT_HOME} ${CARNIVAL_MICRONAUT_HOME}/.

COPY --from=builder ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut-0.1-all.jar \
                    ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut.jar


CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} \
      -Dcarnival.home=${CARNIVAL_MICRONAUT_HOME} \
      -Dlogback.configurationFile=${CARNIVAL_MICRONAUT_HOME}/config/logback.xml \
      -Dmicronaut.config.files=${CARNIVAL_MICRONAUT_HOME}/config/application.yml \
      -jar ${CARNIVAL_MICRONAUT}/build/libs/carnival-micronaut.jar
