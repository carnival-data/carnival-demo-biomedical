package example.carnival.micronaut



import javax.inject.Singleton

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.core.graph.CoreGraph
import carnival.core.graph.CoreGraphTinker



/**
 * Define a class of objects, CarnivalTinkerGraph, that implement CarnivalGraph.
 * Micronaut will automatically create a singleton object of this class.
 * The annotation @Singleton is used, but is also the default.
 *
 */
@Singleton
class CarnivalTinkerGraph implements CarnivalGraph {

	/** a Carnival core graph */
    CoreGraph coreGraph


    /** no argument constructor that opens an in-memory core graph */
    CarnivalTinkerGraph() {
    	this.coreGraph = CoreGraphTinker.create()
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
    }
}