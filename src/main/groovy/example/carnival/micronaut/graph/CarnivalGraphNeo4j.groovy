package example.carnival.micronaut.graph



import javax.inject.Singleton
import javax.annotation.PostConstruct
import javax.inject.Inject
import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Property
import io.micronaut.core.convert.format.MapFormat
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.ConfigurationProperties

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.core.graph.CoreGraph
import carnival.core.graph.CoreGraphNeo4j
import carnival.core.graph.VertexLabelDefinition




@Singleton
@Requires(notEnv="test")
@Requires(notEnv="testreport")
@Requires(property="carnival-micronaut.graph.runtime", value="neo4j")
class CarnivalGraphNeo4jDefault extends CarnivalGraphNeo4j {
}


@Singleton
@Requires(env="test")
@Requires(notEnv="testreport")
@Requires(property="carnival-micronaut.graph.test", value="neo4j")
class CarnivalGraphNeo4jTest extends CarnivalGraphNeo4j {
}


@Singleton
@Requires(env="testreport")
class CarnivalGraphNeo4jTestReport extends CarnivalGraphNeo4j {
}


@Slf4j
abstract class CarnivalGraphNeo4j extends CarnivalGraph {

    /////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    /////////////////////////////////////////////////////////////////////////////////////

	/** a Carnival core graph */
    CoreGraph coreGraph



    /////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////////////////////////////////////////

    void resetGraphFrom(File dir) {
        assert dir
        assert coreGraph

        coreGraph.close()
        CoreGraphNeo4j.resetGraphFrom(dir)
        startGraph()        
    }
    

    void resetGraphFrom(String dirName) {
        assert dirName
        assert coreGraph

        coreGraph.close()
        CoreGraphNeo4j.resetGraphFrom(dirName)
        startGraph()        
    }


    void startGraph() {
    	coreGraph = CoreGraphNeo4j.create()
        coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            String packageName = this.getClass().getPackage().getName()
            coreGraph.initializeGremlinGraph(graph, g, packageName)
        }
    }


    /**
     * Life-cycle hook to initialize the core graph with the models defined in
     * this project.
     *
     */
    @PostConstruct 
    void initialize() {
        log.trace "\n\n\n\n\n\nCarnivalGraphNeo4j\n\n\n\n\n"
        startGraph()
    }
    

    /** convenience getter for the underlying gremlin graph */
    Graph getGremlinGraph() {
    	coreGraph.graph
    }


    /**x
     * Method to reset the core graph, meant to be used only by tests.
     *
     */
    void resetCoreGraph() {
        coreGraph.close()
        CoreGraphNeo4j.clearGraph()
        initialize()
    }

}