# Carnival-Micronaut

[Carnival](https://github.com/carnival-data/carnival) is a graph framework that allows for a large variety of ETL and analysis tasks related to relational data and property graphs. For this demonstration project, we will show how to use Carnival to harmonize relational data from various sources into a Carnival property graph, and present some of the ways the Carnival graph can be manipulated and analyzed. We use the the Carnival and [Micronaut](https://micronaut.io/) frameworks to create a JVM application that harmonizes relational clinical data from several sources into a Neo4J graph database, and presents a restful API to access and manipulate the data.

This demonstration been set up as a Docker multi-container project, with a container that holds the Carnival/Micronaut server applications and other containers that have databases with test data.

* Instructions on running the entire project are found below.
* A developer walkthrough of Carnival application can be found here: [Developer Walkthrough](docs/walkthrough.md)

## Running the Project
## Set up instructions with Docker

tl;dr

```
Install git
Install Docker Desktop
Create GitHub Personal Access Token with read:packages rights, https://github.com/settings/tokens
git clone https://github.com/carnival-data/carnival-micronaut.git
cd carnival-micronaut
edit .env-template, save as .env
sudo docker-compose build
sudo docker-compose up
Open a browser to http://localhost:5858

To run the database test
docker-compose -f docker-compose-test.yml up

ctrl+c to stop
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
