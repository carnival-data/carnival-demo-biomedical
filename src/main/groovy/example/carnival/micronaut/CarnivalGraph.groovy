package example.carnival.micronaut



import javax.inject.Singleton

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.core.graph.CoreGraph
import carnival.core.graph.CoreGraphTinker



/**
 * Define an interface that Micronaut can use to create a bean, which by
 * default will be a singleton.
 *
 */
interface CarnivalGraph {

	/** the core graph object */
    CoreGraph getCoreGraph()
}



/**
 * Define a class of objects, CarnivalTinkerGraph, that implement CarnivalGraph.
 * Micronaut will automatically create a singleton object of this class.
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
}