package example.carnival.micronaut



import groovy.transform.CompileStatic
import groovy.json.JsonOutput
import groovy.transform.ToString

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Body
import io.micronaut.http.MediaType
import io.micronaut.http.HttpResponse

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Vertex

import carnival.core.graph.Core



@CompileStatic
@Controller("/graphadmin") 
class GraphAdminController {

    protected final CarnivalGraph carnivalGraph


    GraphAdminController(CarnivalGraph carnivalGraph) {
        this.carnivalGraph = carnivalGraph
    }


    @Get("/") 
    @Produces(MediaType.TEXT_PLAIN) 
    String index() {
        def numVertices
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            numVertices = g.V().count().next()
        }
        
        "There are $numVertices vertices in the graph." 
    }

}