package example.carnival.micronaut



import java.text.ParseException
import javax.inject.Inject
import spock.lang.Specification
import spock.lang.Shared
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import example.carnival.micronaut.graph.CarnivalGraph




@MicronautTest
class ExampleSpec extends Specification {

    
    @Shared @Inject CarnivalGraph carnivalGraph
    @Shared Graph graph
    @Shared GraphTraversalSource g


    def setupSpec() {
        carnivalGraph.resetCoreGraph()
        graph = carnivalGraph.coreGraph.graph
    }


    def setup() {
        g = carnivalGraph.coreGraph.traversal()
    }


    def cleanup() {
        if (g) g.close()
        if (graph?.features().graph().supportsTransactions()) {
            graph.tx().rollback()
            //graph.tx().commit()
        }
    }


    def cleanupSpec() {
        if (graph) graph.close()
    }


    ///////////////////////////////////////////////////////////////////////////
    // TESTS
    ///////////////////////////////////////////////////////////////////////////

    void "test create and link vertices"() {
        when:
        def numVertices1 = g.V().count().next()
        def dottieV = GraphModel.VX.DOGGIE.instance().withProperties(
            GraphModel.PX.IS_ADORABLE, true
        ).create(graph)
        def numVertices2 = g.V().count().next()

        then:
        dottieV != null
        numVertices2 == numVertices1 + 1

        when:
        def dottieNameV = GraphModel.VX.NAME.instance().withProperties(
            GraphModel.PX.TEXT, 'Dottie'
        ).ensure(graph, g)
        def numVertices3 = g.V().count().next()

        then:
        dottieNameV != null
        numVertices3 == numVertices2 + 1

        when:
        GraphModel.EX.HAS_BEEN_CALLED.instance().from(dottieV).to(dottieNameV).create()

        then:
        g.V(dottieV)
            .out(GraphModel.EX.HAS_BEEN_CALLED)
            .isa(GraphModel.VX.NAME)
            .has(GraphModel.PX.TEXT, 'Dottie')
        .tryNext().isPresent()
    }
    void "test create and link patient vertices"() {
        when:
        def numVertices1 = g.V().count().next()
        def p1V = GraphModel.VX.PATIENT.instance().withProperties(
                GraphModel.PX.ID, "123"
        ).create(graph)
        def numVertices2 = g.V().count().next()

        then:
        p1V != null
        // p1v == null
        numVertices2 == numVertices1 + 1

        when:
        def e1V = GraphModel.VX.ENCOUNTER.instance().withProperty(
                GraphModel.PX.ID, "abc").withProperty(
                GraphModel.PX.START, "2021").withProperty(
                GraphModel.PX.END, "2022"
        ).ensure(graph, g)

        then:
        e1V != null

        when:
        def edge1E = GraphModel.EX.PATIENT_HAS_ENCOUNTER.instance().from(p1V).to(e1V).create()

        then:
        g.V(p1V)
            .out(GraphModel.EX.PATIENT_HAS_ENCOUNTER)
            .isa(GraphModel.VX.ENCOUNTER)
            .has(GraphModel.PX.END, '2022')
        .tryNext().isPresent()
        // edge1E == null
    }


}