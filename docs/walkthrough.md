# Walkthrough

Carnival is a graph framework that allows for a large variety of ETL and analysis tasks related to relational data and property graphs. For this demonstration project, we will show how to use Carnival to harmonize relational data from various sources into a Carnival property graph, and present some of the ways the Carnival graph can be manipulated and analyzed.

To motivate this, we will be acting in the role of a biomedical researcher who has a research question and data about patients, healthcare encounters and related information (lab tests, medications, health survey answers, etc.) collected in databases and spreadsheets. We want use Carnival to harmonize the data, determine if there are any patient populations that meet the criteria to be included in the research project, and then do some graph analysis on the data and produce some reports.

This example will cover:
* How to set up a new Carnival project
* Defining a Carnival graph model
* Combining data from different sources in a Carnival property graph
* Executing graph operations to draw conclusions about the data
* Examining the providence of data in the graph
* Presenting an API that allows users to do some basic graph exploration and analysis
* Examining the property graph directly


## Setup
### Docker
### Micronaut app (gradle setup etc)

## Defining the graph model

## Connecting the data
### EHR Data in Postgres
### CSV file data

## Doing graph operations
### Drawing conclusions

## Key Idea - Providence

## Presenting an API with Micronaut
### Finding the patient cohort
### Generating a report

## Examining the Graph in Neo4j Desktop
