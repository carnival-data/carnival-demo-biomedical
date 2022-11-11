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

import carnival.core.Carnival
import carnival.core.CarnivalNeo4j
import carnival.core.CarnivalNeo4jConfiguration

import example.carnival.micronaut.config.AppConfig



///////////////////////////////////////////////////////////////////////////////
// The classes below will optionally create a BiomedCarnival bean based on the
// configuration.  Only one bean will be created, depending on the individual
// configurations requirements, which were designed to make these bean
// bean declarations mutually exclusive.
///////////////////////////////////////////////////////////////////////////////

@Singleton
@Requires(notEnv="test")
@Requires(notEnv="testreport")
@Requires(property="carnival-micronaut.graph.runtime", value="neo4j")
class BiomedCarnivalNeo4jDefault extends BiomedCarnivalNeo4j {
}


@Singleton
@Requires(env="test")
@Requires(notEnv="testreport")
@Requires(property="carnival-micronaut.graph.test", value="neo4j")
class BiomedCarnivalNeo4jTest extends BiomedCarnivalNeo4j {
}


@Singleton
@Requires(env="testreport")
class BiomedCarnivalNeo4jTestReport extends BiomedCarnivalNeo4j {
}



///////////////////////////////////////////////////////////////////////////////
// If a BiomediCarnivalNeo4j bean is created via the above declarations, it
// will be a simple extension of the abstract class defined below.
///////////////////////////////////////////////////////////////////////////////

/**
 * BiomedCarnivalNeo4j implements a BiomedCarnival using an underlying 
 * CarnivalNeo4j object.  Because BiomedCarnivalNeo4j is abstract, it will not
 * be instantiated as a Micronaut bean.
 *
 */
@Slf4j
abstract class BiomedCarnivalNeo4j extends BiomedCarnival {

    /////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    /////////////////////////////////////////////////////////////////////////////////////

    /** app configuration */
    @Inject AppConfig appConfig

	/** the underlying Carnival */
    CarnivalNeo4j carnival


    /////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the CarnivalNeo4j configuration from the app config.
     *
     */
    CarnivalNeo4jConfiguration getCarnivalNeo4jConfig() {
        appConfig.carnival.neo4j
    }
    

    /**
     * Create and start the underlying Neo4j Carnival.
     *
     */
    void startCarnival() {
    	carnival = CarnivalNeo4j.create(carnivalNeo4jConfig)
        initGremlinGraph()
    }


    /**x
     * Method to reset the core graph, meant to be used only by tests.
     *
     */
    void resetCarnival() {
        carnival.close()
        CarnivalNeo4j.clearGraph(carnivalNeo4jConfig)
        startCarnival()
    }


    /**
     * Life-cycle hook to initialize the core graph with the models defined in
     * this project.
     *
     */
    @PostConstruct 
    void initialize() {
        log.trace "\n\n\n\n\n\nCarnivalNeo4j\n\n\n\n\n"
        startCarnival()
    }


    /** convenience getter for the underlying gremlin graph */
    Graph getGremlinGraph() {
    	carnival.graph
    }

}