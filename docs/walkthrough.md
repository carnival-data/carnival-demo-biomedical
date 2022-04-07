# Carnival Developer Walkthrough

Carnival is a graph framework that allows for a large variety of ETL and analysis tasks related to relational data and property graphs. For this demonstration project, we will show how to use Carnival to harmonize relational data from various sources into a Carnival property graph, and present some of the ways the Carnival graph can be manipulated and analyzed.

To motivate this, we will be acting in the role of a biomedical researcher who has a research question and data about patients, healthcare encounters and related information (lab tests, medications, health survey answers, etc.) collected in databases and spreadsheets. We want to use Carnival to harmonize the data, determine if there are any patient populations that meet the criteria to be included in the research project, and then do some graph analysis on the data and produce some reports.

This example will cover:
* How to set up a new Carnival application
* Defining a Carnival graph model
* Combining data from different sources in a Carnival property graph
* Executing graph operations to draw conclusions about the data
* Examining the providence of data in the graph
* Presenting an API that allows users to do some basic graph exploration and analysis
* Examining the property graph directly

> *Note: This demonstration is set up as Docker multi-container project, with one container holds the Carnival application and other containers that contain example databases. This walkthrough focuses excusively on the Carnival application. See [Running the Project](https://github.com/carnival-data/carnival-micronaut/blob/master/README.md#running-the-project) for instrucitons on running the entire project.*


# Research Problem
For this example the researcher is looking for a case and control cohort of patients that meet certian criteria:

*IN PROGRESS, pending info on EHR data*

>Cases
>* Age - between 18 and 55
>* At least two healthcare encounters with indications of *DISEASE* via *CRITERIA*
>* Has been prescribed medication *XXX*
>* Self-reported social history of drinking or smoking
>
>Controls
>* Age - between 18 and 55
>* No indications of *DISEASE* via *CRITERIA*
>* Has not been prescribes medication *XXX*
>* Self-reported social history of drinking or smoking


## Examining the Source Data
There are two synthetic relational datasources:

* **Electronic Heath Records(EHR) data** is stored in a Postgres database. The data is in **XXX** format.
* **Self-reported patient survey data** is stored in csv spreadsheets


### EHR Data in Postgres

The EHR data is represented by synthetically-generated **XXX** formated dataset that contains information about patients and heathcare encounters. This data was generated using **XXX** (synthea?)


For this example, we set up a Postgres database in a docker container using the default [Postgres docker image](https://hub.docker.com/_/postgres/). The raw data is located in `data/db`, and the image has been configured to automatically load the data when docker is started.


Once docker has been started using `docker-compose up`, the database can be browsed using database GUI tool like DBeaver with the credentials:
* Host: localhost
* Port: 5433 (not the standard port!)
* Database: EHR
* Username: postgres
* Password: postgres


### Self-reported survey data in CSV file data

CSV files that contain the results of self-reported surveys are located in `data/survey`.

# Carnival
This demonstration been set up as a Docker multi-container project, with a container that holds the Carnival/Micronaut server applications and other containers that have databases with test data. The source data for the carnival server is located in `/src`


## Creating a new Carnival Project

## Defining the graph model
Now that we have examined the data and created an initial carnival project, we want to create a data model that harmonizes patient and encounter data and the survey data.

## Doing graph operations
### Drawing conclusions

## Key Idea - Providence

## Presenting an API with Micronaut
### Finding the patient cohort
### Generating a report

## Examining the Graph in Neo4j Desktop
