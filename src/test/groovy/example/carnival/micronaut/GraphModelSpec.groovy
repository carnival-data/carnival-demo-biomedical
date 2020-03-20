package example.carnival.micronaut



import java.util.function.BiConsumer

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.http.MediaType

import spock.lang.Specification

import javax.inject.Inject

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.structure.T

import carnival.graph.Base
import carnival.core.graph.Core



@MicronautTest 
class GraphModelSpec extends Specification {


    ///////////////////////////////////////////////////////////////////////////
    // APPLICATION COMPONENTS
    ///////////////////////////////////////////////////////////////////////////

    @Inject
    ApplicationContext ctx

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    CarnivalGraph carnivalGraph



    ///////////////////////////////////////////////////////////////////////////
    // TEST LIFE CYCLE
    ///////////////////////////////////////////////////////////////////////////

    def setup() {
        carnivalGraph.resetCoreGraph()
    }


    ///////////////////////////////////////////////////////////////////////////
    // TESTS
    ///////////////////////////////////////////////////////////////////////////


    void "test initial graph"() {
        expect:
        carnivalGraph.coreGraph.checkConstraints().size() == 0
        carnivalGraph.coreGraph.checkModel().size() == 0
    }


    void "test BaseModel is modelled"() {
        when:
        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            GraphModel.VX.PERSON.instance()
                .withProperties(
                    GraphModel.PX.ID, '58',
                    Core.PX.NAME, 'alex'
                )
            .vertex(graph, g)
        }

        then:
        carnivalGraph.coreGraph.checkConstraints().size() == 0
        carnivalGraph.coreGraph.checkModel().size() == 0
    }


    /*void "test unmodelled property"() {
        when:
        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            graph.addVertex(
                T.label, 
                GraphModel.VX.PERSON.label,
                Base.PX.NAME_SPACE.label, 
                GraphModel.VX.PERSON.nameSpace, 
                GraphModel.PX.COLOR.label, 
                'red'
            )
        }

        then:
        carnivalGraph.coreGraph.checkConstraints().size() == 0
        carnivalGraph.coreGraph.checkModel().size() == 0

    }*/

}