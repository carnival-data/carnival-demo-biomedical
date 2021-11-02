# Carnival Micronaut

Demonstration Micronaut server with a Carnival graph resource.

## Set up instructions with Docker

tl;dr

```
Install git
Install Docker Desktop
Create GitHub Personal Access Token with read:packages rights
git clone https://github.com/pmbb-ibi/carnival-micronaut.git
cd carnival-micronaut
edit .env-template, save as .env
sudo docker-compose build
sudo docker-compose up
Open a browser to http://localhost:5858
```

Prerequisites: Docker and git

On [Windows](https://docs.docker.com/desktop/windows/install/) and [Mac](https://docs.docker.com/desktop/mac/install/), install Docker Desktop. 

Note that as non-profit institutions, we can use the free "Personal" license.

[Docker Licensing FAQ](https://www.docker.com/pricing/faq)
*I am a researcher at a university (or another not-for-profit institution); do I or my research assistants need to purchase a Pro, Team, or Business subscription to use Docker Desktop?*

*No. You and your assistants may use Docker Desktop for free with a Docker Personal subscription.*

### 1. Set up GitHub

Set up your GitHub environment to work with [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry).

Generate a [Personal Access Token](https://github.com/settings/tokens) with read:packages rights. Be sure to save it as you won't be able to look it up on GitHub later.

### 2. Clone Carnival Micronaut

```
git clone https://github.com/pmbb-ibi/carnival-micronaut.git
```

### 3. Put credentials in .env

```
cd carnival-micronaut
edit .env-template, save as .env
```

Edit .env-template, inserting your GitHub username and access token. Save as .env


### 4. Build and run the Hello World app

```
sudo docker-compose build
sudo docker-compose up
```

There should now be a server running at http://localhost:5858.

## Set up instructions without Docker

Prerequisite: JDK 11

### 1. Set up GitHub

Set up your GitHub environment to work with [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry).

### 2. Clone Carnival Micronaut

```
git clone https://github.com/pmbb-ibi/carnival-micronaut.git
```

<!--
### 3. Create Home Directory

The Carnival Micronaut Home directory will us the working directory for Carnival Micronaut.  It will include all configuration and data.

Set an environment variable to point to the home directory:

```
export CARNIVAL_MICRONAUT_HOME=/full/path/to/carnival-micronaut/carnival-micronaut-home
```
-->

### 3. Build and run the Hello World app

```
cd carnival-micronaut
./gradlew run
```

There should now be a server running at http://localhost:5858.


### 4. Create and run Docker container

```
./docker-build.zsh
docker run --publish 5858:5858 carnival-micronaut:0.1
```

## Testing with Docker
Run tests with the following:
```
docker-compose -f docker-compose-test.yml up
```
This will run the unit tests. The test results will be printed in the docker log. An exit code of 0 will be returned if the tests pass, otherwise a non-zero code will be returned.