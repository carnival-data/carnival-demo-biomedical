package example.carnival.micronaut.graph



import groovy.util.logging.Slf4j
import javax.inject.Singleton
import javax.annotation.PostConstruct
import io.micronaut.context.annotation.Requires

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.core.Carnival
import carnival.core.CarnivalTinker



@Singleton
@Requires(notEnv="test")
@Requires(notEnv="testreport")
@Requires(property="carnival-micronaut.graph.runtime", value="tinker")
class BiomedCarnivalTinkerDefault extends BiomedCarnivalTinker {
}


@Singleton
@Requires(env="test")
@Requires(notEnv="testreport")
@Requires(property="carnival-micronaut.graph.test", value="tinker")
class BiomedCarnivalTinkerTest extends BiomedCarnivalTinker {
}


/**
 * Define a class of objects, BiomedCarnivalTinker, that implement BiomedCarnival.
 * Micronaut will automatically create a singleton object of this class.
 * The annotation @Singleton is used, but is also the default.
 *
 */
@Slf4j
class BiomedCarnivalTinker extends BiomedCarnival {

	/** a Carnival core graph */
    CarnivalTinker carnival


    /** no argument constructor that opens an in-memory core graph */
    BiomedCarnivalTinker() {
    	carnival = CarnivalTinker.create()
    }


    /**
     * Life-cycle hook to initialize the unerlying carnival with the models 
     * defined in this project.
     *
     */
    @PostConstruct 
    void initialize() {
        log.trace "\n\n\n\n\nBiomedCarnivalTinker\n\n\n\n\n"
        initGremlinGraph()
    }
    

    /** convenience getter for the underlying gremlin graph */
    Graph getGremlinGraph() {
    	carnival.graph
    }
    

    /**
     * Method to reset the core graph, meant to be used only by tests.
     *
     */
    void resetCarnival() {
        carnival.close()
        this.carnival = CarnivalTinker.create()
        initialize()
    }
}