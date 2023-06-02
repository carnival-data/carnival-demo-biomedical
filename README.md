[![Docker CI](https://github.com/carnival-data/carnival-demo-biomedical/actions/workflows/DockerCI.yml/badge.svg)](https://github.com/carnival-data/carnival-demo-biomedical/actions/workflows/DockerCI.yml)

# Carnival-Micronaut

[Carnival](https://github.com/carnival-data/carnival) is a graph framework that allows for a large variety of ETL and analysis tasks related to relational data and property graphs. For this demonstration project, we will show how to use Carnival to harmonize relational data from various sources into a Carnival property graph, and present some of the ways the Carnival graph can be manipulated and analyzed. We use the the Carnival and [Micronaut](https://micronaut.io/) frameworks to create a JVM application that harmonizes relational clinical data from several sources into a Neo4J graph database, and presents a restful API to access and manipulate the data.

This demonstration been set up as a Docker multi-container project, with a container that holds the Carnival/Micronaut server applications and other containers that have databases with test data.

* Instructions on running the entire project are found below.
* A developer walkthrough of Carnival application can be found here: [Developer Walkthrough](docs/walkthrough.md)

## Running the Project

## Quickstart

To just run the demo, do the following:

```
Install git
Install Docker Desktop
git clone https://github.com/carnival-data/carnival-demo-biomedical.git
cd carnival-demo-biomedical
docker compose build
docker compose up

Open a browser to check API endpoints:
http://localhost:5858
http://localhost:5858/case_patients
http://localhost:5858/control_patients

ctrl+c to stop
```

## Detailed Setup
### Prerequisites: Docker and git

On [Windows](https://docs.docker.com/desktop/windows/install/) and [Mac](https://docs.docker.com/desktop/mac/install/), install Docker Desktop. 


Note that as non-profit institutions, we can use the free "Personal" license.

[Docker Licensing FAQ](https://www.docker.com/pricing/faq)
*I am a researcher at a university (or another not-for-profit institution); do I or my research assistants need to purchase a Pro, Team, or Business subscription to use Docker Desktop?*

*No. You and your assistants may use Docker Desktop for free with a Docker Personal subscription.*

### 1. Clone Carnival Micronaut

```
git clone https://github.com/carnival-data/carnival-demo-biomedical.git
cd carnival-demo-biomedical
```

### 2. Build and run the app

```
docker compose build
docker compose up
```

After a few minutes, there should be a server running at `http://localhost:5858`.

API endpoints will serve JSON responses containing the case and control cohorts ([API Documentation](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/docs/ResearchAnswersApi.raml)):

```
http://localhost:5858/case_patients
http://localhost:5858/control_patients
```

## Set up instructions for running the app with less Docker

Prerequisite: JDK 11, Docker, git

### 1. Clone Carnival Micronaut

```
git clone https://github.com/carnival-data/carnival-demo-biomedical.git
cd carnival-demo-biomedical
```

<!--
### 3. Create Home Directory

The Carnival Micronaut Home directory will us the working directory for Carnival Micronaut.  It will include all configuration and data.

Set an environment variable to point to the home directory:

```
export CARNIVAL_MICRONAUT_HOME=/full/path/to/carnival-micronaut/carnival-micronaut-home
```
-->

### 2. Start the database using Docker

```
docker compose build db
docker compose up db
# To run in the background as a daemon use:
# docker compose up -d db
```

### 3. Build and run the app

```
./gradlew run
```

After a few minutes, there should be a server running at http://localhost:5858.

## Testing with Docker
Run tests with the following:
```
docker compose -f docker-compose-test.yml up
```
This will run the unit tests. The test results will be printed in the docker log. An exit code of 0 will be returned if the tests pass, otherwise a non-zero code will be returned.

## Viewing source data in Postgres

Once docker has been started using `docker compose up`, the database can be browsed using database GUI tool like DBeaver with the credentials:
* Host: localhost
* Port: 5433, or as specified in `.env`
* Database: EHR
* Username: postgres
* Password: postgres
