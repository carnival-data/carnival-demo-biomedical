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



    ///////////////////////////////////////////////////////////////////////////
    // SERVICE METHOD
    ///////////////////////////////////////////////////////////////////////////


    /** */
    public void syncExample (Map args = [:]) {

        carnivalGraph.coreGraph.withTraversal { graph, g ->

            exampleMethods.method('LoadEncounters').call(graph, g)

        }


    }

}