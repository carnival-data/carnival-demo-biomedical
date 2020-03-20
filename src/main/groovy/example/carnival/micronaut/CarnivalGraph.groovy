package example.carnival.micronaut



import carnival.core.graph.CoreGraph



/**
 * The CarnivalGraph interface wraps a CoreGraph object.
 *
 * Defines an interface that Micronaut can use to create a bean, which by
 * default will be a singleton.
 *
 */
interface CarnivalGraph {

	/** the core graph object */
    CoreGraph getCoreGraph()
}