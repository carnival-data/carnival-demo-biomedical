package example.carnival.micronaut



import carnival.core.graph.CoreGraph



/**
 * Define an interface that Micronaut can use to create a bean, which by
 * default will be a singleton.
 *
 */
interface CarnivalGraph {

	/** the core graph object */
    CoreGraph getCoreGraph()
}