# carnival-micronaut

Example [Micronaut](https://micronaut.io) application that uses [Carnival](https://github.com/pmbb-ibi/carnival).

## Quick Start

From the commmand line:

```
git clone https://github.com/augustearth/carnival-micronaut.git
cd carnival-micronaut
./gradlew compileGroovy
./gradlew run --console=plain
```

Go to `http://localhost:7000/` in a web browser.  You should see a blank web page with only the text `carnival-micronaut`.


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
./gradlew test --tests="example.carnival.micronaut.GraphControllerSpec"
./gradlew test --tests="example.carnival.micronaut.GraphControllerSpec.create vertex"
```

## Run
carnival-micronaut can be run via Gradle.

To run on the default port of `7000`:

```
./gradlew run --console=plain
```


## Docker
carnival-micronaut can be built and run as a Docker image.

To build the Docker image:

```
./gradlew jibDockerBuild

```

To run the Docker container on default port `7000` on the host machine:

```
docker run -it -p 7000:7000 carnival-micronaut:0.1
```

To run the Docker container on port `3000` on the host machine:

```
docker run -it -p 3000:7000 carnival-micronaut:0.1
```
