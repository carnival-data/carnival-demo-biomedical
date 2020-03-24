package example.carnival.micronaut



import javax.inject.Singleton
import javax.annotation.PostConstruct

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.core.graph.CoreGraph
import carnival.core.graph.CoreGraphTinker
import carnival.core.graph.VertexLabelDefinition



/**
 * Define a class of objects, CarnivalGraphTinker, that implement CarnivalGraph.
 * Micronaut will automatically create a singleton object of this class.
 * The annotation @Singleton is used, but is also the default.
 *
 */
@Singleton
class CarnivalGraphTinker implements CarnivalGraph {

	/** a Carnival core graph */
    CoreGraph coreGraph


    /** no argument constructor that opens an in-memory core graph */
    CarnivalGraphTinker() {
    	coreGraph = CoreGraphTinker.create()
    }


    /**
     * Life-cycle hook to initialize the core graph with the models defined in
     * this project.
     *
     */
    @PostConstruct 
    void initialize() {
        coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            String packageName = this.getClass().getPackage().getName()
            coreGraph.initializeGremlinGraph(graph, g, packageName)
        }
    }
    


    /** convenience getter for the underlying gremlin graph */
    Graph getGremlinGraph() {
    	coreGraph.graph
    }

    /**
     * Method to reset the core graph, meant to be used only by tests.
     *
     */
    void resetCoreGraph() {
        coreGraph.close()
        this.coreGraph = CoreGraphTinker.create()
        initialize()
    }
}