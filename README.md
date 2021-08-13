# Carnival Micronaut

Demonstration Micronaut server with a Carnival graph resource.


# Set up instructions

Prerequisite: JDK 11

## 1. Set up GitHub

Set up your GitHub environment to work with [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry).


## 2. Create Home Directory

The Carnival Micronaut Home directory will us the working directory for Carnival Micronaut.  It will include all configuration and data.

Clone the default home directory:

```
git clone https://github.com/pmbb-ibi/carnival-micronaut-home.git
```

Set an environment variable to point to the directory:

```
export CARNIVAL_MICRONAUT_HOME=/full/path/to/carnival-micronaut-home
```

## 3. Clone Carnival Micronaut

```
git clone https://github.com/pmbb-ibi/carnival-micronaut.git
```

## 4. Build and run the Hello World app

```
cd carnival-micronaut
./gradlew run
```

There shoule now be a server running at http://localhost:5858.


## 5. Create and run Docker container

```
./docker-build.zsh
docker run --publish 5858:5858 carnival-micronaut:0.1
```
