package example.carnival.micronaut.method



import groovy.transform.ToString
import groovy.util.logging.Slf4j
import javax.inject.Singleton
import javax.inject.Inject

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import carnival.core.graph.GraphMethods
import carnival.core.graph.GraphMethod

import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.vine.ExampleDbVine
import example.carnival.micronaut.graph.CarnivalGraph



@ToString(includeNames=true)
@Slf4j 
@Singleton
class ExampleMethods implements GraphMethods { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject AppConfig config
    @Inject ExampleDbVine exampleDbVine
    @Inject CarnivalGraph carnivalGraph




    ///////////////////////////////////////////////////////////////////////////
    // SERVICE METHOD
    ///////////////////////////////////////////////////////////////////////////


    /** */
    class LoadEncounters extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def mdt = exampleDbVine
                .method('Encounters')
                .call()
            .result

            mdt.data.values().each { rec ->
                log.trace "rec: ${rec}"
                GraphModel.VX.ENCOUNTERS.instance().withNonNullMatchingProperties(rec).ensure(graph, g)
            }

        }

    }






}