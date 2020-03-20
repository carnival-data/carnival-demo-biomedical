# carnival-micronaut

Example [Micronaut](https://micronaut.io) application that uses [Carnival](https://github.com/pmbb-ibi/carnival).

## Build
carnival-micronaut is built using Gradle.  There is a Gradle wrapper provided.

```
./gradlew clean
./gradlew compileGroovy
```

## Test
carnival-micronaut is testing using the Gradle testing mechanisms.

```
./gradlew test
./gradlew test --tests="example.carnival.micronaut.PersonControllerSpec"
./gradlew test --tests="example.carnival.micronaut.PersonControllerSpec.test person get by name"
```

## Run
carnival-micronaut can be run via Gradle.

To run on the default port of `8080`:

```
./gradlew run --console=plain
```


## Docker
carnival-micronaut can be built and run as a Docker image.

To build the Docker image:

```
./gradlew jibDockerBuild

```

To run the Docker container on port `3000` on the host machine:

```
docker run -it -p 3000:8080 carnival-micronaut:0.1
```
