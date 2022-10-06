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

> *Note: This demonstration is set up as Docker multi-container project, with one container holds the Carnival application and other containers that contain example databases. This walkthrough focuses excusively on the Carnival application. See [Running the Project](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/README.md#running-the-project) for instrucitons on running the entire project.*


# Research Problem
For this example the researcher is looking for a case and control cohort of patients that meet certain criteria:

>Cases
>* Aged between 18 and 35, inclusive
>* At least two healthcare encounters with indications of *employment*
>* Diagnosis of *hypertension*
>* Has been prescribed medication *lisinopril*
>* Self-reported social history of *smoking*

>Controls
>* Aged between 18 and 35, inclusive
>* Does not have two or more healthcare encounters with indications of *employment*
>* No diagnosis of *hypertension*
>* Has been prescribed medication lisinopril
>* Self-reported social history of *smoking*


## Examining the Source Data
There are two synthetic relational datasources:

* **Electronic Heath Records(EHR) data** is relational data loaded into a Postgres database
* **Self-reported patient survey data** is stored in csv spreadsheets


### EHR Data in Postgres

The EHR data is represented by synthetically-generated datasets that contain information about patients and heathcare encounters. This data was generated using [Synthea](https://synthetichealth.github.io/synthea/), using the configuration details found [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/data/db/synthea/readme.md).


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


## Defining the graph model
We have defined a simple carnival data model for our graph that that describes patients, healthcare encounters, medications and conditions [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/GraphModel.groovy). 

## Loading the data into the graph
Data is loaded into the graph using vine methods. The vines can be found [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/vine/ExampleDbVine.groovy).

## Graph Reasoning
We have defined reasoner methods to search for the patient cohorts defined in this example and update the graph with this information. The reasonsers can be found [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/method/Reasoners.groovy)

## Presenting an API with Micronaut
Using the micronaut framework, we present an api with two endpoints to return the case and control patient cohorts in the graph. The endpoints are defined [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/web/AppWS.groovy).

The endpoints are:
```
http://localhost:5858
http://localhost:5858/cohort_patients
http://localhost:5858/control_patients
```
