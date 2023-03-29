package example.carnival.micronaut.graph



import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.core.Carnival



/**
 * BiomedCarnival is an abstract class that defines the core data object of
 * this project.  It is a wrapper for an underlying Carnival object.
 *
 */
abstract class BiomedCarnival {

	/** the underlying carnival object */
    abstract Carnival getCarnival()

    /** the gremlin graph from the underlying carnival object */
    abstract Graph getGremlinGraph()

    /** */
    abstract void resetCarnival()

    /**
     * Initialize the underlying Carnival with the models provided in this
     * project.
     */
    void initGremlinGraph() {
        carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            String packageName = this.getClass().getPackage().getName()
            carnival.addModelsFromPackage(graph, g, packageName)
        }
    }

    /** 
     * return a boolean indicator of whether the graph engine of the 
     * underlying carnival object supports transactions
     */
    boolean supportsTransactions() {
        carnival.graph.features().graph().supportsTransactions()        
    }

    /** Convenience method to commit the current transaction */
    void commit() {
        if (supportsTransactions()) {
            carnival.graph.tx().commit()
        }
    }

}