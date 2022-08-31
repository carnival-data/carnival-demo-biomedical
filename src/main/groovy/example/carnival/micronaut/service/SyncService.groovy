package example.carnival.micronaut.service



import groovy.transform.ToString
import groovy.util.logging.Slf4j
import javax.inject.Singleton
import javax.inject.Inject

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.text.StringEscapeUtils
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__

import carnival.util.GenericDataTable
import carnival.util.MappedDataTable
import carnival.core.vine.Vine

import example.carnival.micronaut.GraphModel
import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.vine.ExampleDbVine
import example.carnival.micronaut.method.ExampleMethods
import example.carnival.micronaut.graph.CarnivalGraph
import example.carnival.micronaut.method.Reasoners




@ToString(includeNames=true)
@Slf4j 
@Singleton
class SyncService { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    final ObjectMapper objectMapper = new ObjectMapper()

    @Inject AppConfig config
    @Inject ExampleDbVine exampleDbVine
    @Inject CarnivalGraph carnivalGraph
    @Inject ExampleMethods exampleMethods
    @Inject Reasoners reasoners



    ///////////////////////////////////////////////////////////////////////////
    // SERVICE METHOD
    ///////////////////////////////////////////////////////////////////////////


    /** */
    public void syncExample (Map args = [:]) {

        carnivalGraph.coreGraph.withTraversal { graph, g ->
        
            log.info("Loading Patients")
            exampleMethods.method('LoadPatients').call(graph, g)
            log.info("Loading Encounters")
            exampleMethods.method('LoadEncounters').call(graph, g)
            log.info("Loading Conditions")
            exampleMethods.method('LoadConditions').call(graph, g)
            log.info("Loading Medications")
            exampleMethods.method('LoadMedications').call(graph, g)
            log.info("Loading Surveys")
            exampleMethods.method('LoadSurveys').call(graph, g)

            log.info("Searching graph for case patients")
            reasoners.method('FindResearchAnswer').call(graph, g)
            log.info("Searching graph for control patients")
            reasoners.method('FindControlPatients').call(graph, g)
            log.info("Identified case and control groups successfully")

            System.gc()

            if (graph.features().graph().supportsTransactions()) {
                graph.tx().commit()
                log.info("Graph Committed")
            }

            System.gc()

            log.info("")
            log.info("The following API endpoints should now be reachable:")
            log.info("http://localhost:5858/")
            log.info("http://localhost:5858/case_patients")
            log.info("http://localhost:5858/cohort_patients")
            log.info("Try opening a browser to http://localhost:5858/")
            log.info("")

        }
    }
}