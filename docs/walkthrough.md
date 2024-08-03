# Carnival Developer Walkthrough

Carnival is a graph framework written in [Groovy](https://groovy-lang.org) that allows for a large variety of ETL (extract, transform and load) and analysis tasks related to relational data and property graphs. For this demonstration project, we will show how to use Carnival to harmonize data from several relational sources into a Carnival property graph, and present some of the ways the Carnival graph can be manipulated and analyzed.

> *Note: Basic knowledge of property graph databases is assumed (a good introduction can be found [here](https://kelvinlawrence.net/book/Gremlin-Graph-Guide.html#whygraph)). It might also be helpful to have some awareness of the [Apache TinkerPop API](https://tinkerpop.apache.org/docs/current/reference/), a graph computing framework that allows client code in languages like Java or Groovy to talk to a TinkerPop enabled graphs.*

To frame this example, we will be acting in the role a biomedical researcher who has a research question about smoking and hypertension. They wish to create case and control patient cohorts for a study, and generate a dataset that can be used in a graph based analysis pipeline. They have access to hospital data about patients, healthcare encounters and related information (lab tests, medications, health survey answers, etc.) collected in several databases and spreadsheets. We want to use Carnival to harmonize the data, determine if there are any patient populations that meet the criteria to be included in the research project, and then do some graph analysis on the data and produce some reports.


This example will cover:
* How to set up a new Carnival application
* Defining a Carnival graph model
* Combining data from different sources relational databases in a Carnival property graph
* Executing graph operations to draw conclusions about the data
* Examining the providence of data in the graph
* Presenting an API that allows users to do some basic graph exploration and analysis
* A way to export the graph for analysis using other tools

> *Note: This demonstration is set up as Docker multi-container project, with one container that holds the Carnival application and other containers that contain example databases. This walkthrough focuses primarily on the Carnival application. See [Running the Project](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/README.md#running-the-project) for instrucitons on running the entire project.*


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
For this demonstration we have created two synthetic relational datasources:

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
To start this process, we examine the data and define a simple [Carnival Data Model](https://carnival-data.github.io/carnival/graph-model.html) for our graph that describes the relationship between patients, healthcare encounters, medications and conditions. Core to Carnival is the ability to create and enforce such a model for a property graph, and it is heavily influenced by concepts in [Web Ontology Language(OWL)](https://www.w3.org/TR/2012/REC-owl2-primer-20121211/). Vertices, edges, and properties can all be modelled, and certain validation constraints and even class relationships (similar to [OWL classes](https://en.wikipedia.org/wiki/Web_Ontology_Language#Classes)) can be specified. For this example, we have modelled vertices and properties for major concepts like patients, encounters and medications, modeled edges and edge properties, and added some domain and range validation to the edges to restrict the types of vertices they can connect. The model can be found [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/GraphModel.groovy).

> *Note: The best way to a model graph data is a complex topic that is outside the scope of this demonstration project, but a real world biomedical example of this application is described [in this paper](https://ebooks.iospress.nl/volumearticle/51943). Our modeling decisions were heavily influenced by ontologies, and specifically the [Open Biological and biomedical Ontology Foundry](https://obofoundry.org). A simplified version of that model is used here.*

## Loading the data into the graph
We will extract data from the database and csv files using Carnival data adaptors classes called [Vines](https://carnival-data.github.io/carnival/vines.html). Carnival vines provide a lightweight caching mechanism that can reduce computational burden by reducing repeated queries to a database for the same information. The implementation of vines for this example can be found [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/vine/ExampleDbVine.groovy).

Now that we have a way to access the raw data, we need a way to harmonize it and add it to the graph. In Carnival, the graph can be manipulated using the [Carnival Graph API](https://carnival-data.github.io/carnival/graph-api.html), which is a layer over the standard [TinkerPop API](https://tinkerpop.apache.org/docs/current/reference/) that provides more semantic support then is inherent in property graphs and works with the previously defined carnival graph model. We encapsulate the logic to load the data Carnival [GraphMethods](https://carnival-data.github.io/carnival/graph-method.html). Carnival GraphMethods provide a standard way to access the Carnival Graph API, as well as automatically adding [providence infromation](https://carnival-data.github.io/carnival/graph-method.html#graph-method-provenance) to the graph to record that a process was run. In our example, the graph methods are defined [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/method/ExampleMethods.groovy).

> *Taking it further: For this demonstration we ETL all the data when the application starts, however for a different use case you may want to query and load some data on demand or in batch.*

## Graph Reasoning
We have defined reasoner methods to search for the patient case and control cohorts based on the criteria specified in this example and update the graph with this information. The logic for these methods is also encapsulated in GraphMethods. The reasonsers can be found [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/method/Reasoners.groovy)

## Presenting an API with Micronaut
Using the [micronaut framework](https://micronaut.io), we present a basic api with endpoints to return the case and control patient cohorts and details about the patient and healthcare encounter nodes in the graph. The endpoints are defined in code [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/src/main/groovy/example/carnival/micronaut/web/AppWS.groovy).

The api is documented in RAML format [here](https://github.com/carnival-data/carnival-demo-biomedical/blob/master/docs/ResearchAnswersApi.raml).


The endpoints are:
```
http://localhost:5858
http://localhost:5858/cohort_patients
http://localhost:5858/control_patients
http://localhost:5858/patient/{id}
http://localhost:5858/encounter/{id}
```
> *Going Further: A robust UI is beyond the scope of this demonstration project, but the API could be expanded to work with a UI framework like React or Angular.* 


## Exporting the Graph for Analysis
TinkerPop offers a variety of [graph export options](https://tinkerpop.apache.org/docs/3.3.3/reference/#_gremlin_i_o). For this demonstration, we added API endpoints that exports the graph in [graphML](http://graphml.graphdrawing.org) or [graphson](https://docs.oracle.com/en/database/oracle/property-graph/21.2/spgdg/graphson-data-format.html) format.

As an example, this could be used to transfer graph into a Python ML analysis pipeline that uses [NetworkX](https://networkx.org/documentation/stable/reference/readwrite/graphml.html).

```
http://localhost:5858/export/graphml
http://localhost:5858/export/graphson
```
