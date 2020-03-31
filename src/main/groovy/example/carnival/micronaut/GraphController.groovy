package example.carnival.micronaut



import javax.inject.Inject
import javax.validation.constraints.Size

import groovy.transform.CompileStatic
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.ToString

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single

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
import carnival.core.graph.GraphValidationError



/**
 * GraphController provides administrative functionality over the graph
 * as a whole.
 *
 */
@CompileStatic
@Controller("/graph") 
class GraphController {


    ///////////////////////////////////////////////////////////////////////////
    // STATIC
    ///////////////////////////////////////////////////////////////////////////

    /** */
    static Logger log = LoggerFactory.getLogger(PersonController.class)


    /** */
    @ToString
    static class ModelError {
        String message
    }


    /** */
    static Map toMap(Vertex vert) {
        Map m = [
            vertexId:vert.id(),
            vertexLabel:vert.label()
        ]

        if (vert.keys()) {
            m.put('propertyKeys', vert.keys())

            m.put('properties', new HashMap())
            vert.keys().each { String k ->
                m.properties.put(k, vert.value(k))
            }
        }

        return m
    }


    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    /** */
    @Inject
    CarnivalGraph carnivalGraph



    ///////////////////////////////////////////////////////////////////////////
    // HTTP GET
    ///////////////////////////////////////////////////////////////////////////

    @Get("/validation/check-model") 
    @Produces(MediaType.APPLICATION_JSON)
    Observable<ModelError> checkModel() {
        log.trace "checkModel"

        List<ModelError> modelErrors = carnivalGraph
            .coreGraph
            .checkModel()
        .collect({ new ModelError(message:it) })
        modelErrors.eachWithIndex { e, ei -> log.trace "${ei}: $e"}

        Observable.fromArray((ModelError[])modelErrors.toArray())
    }


    @Get("/validation/check-constraints") 
    @Produces(MediaType.APPLICATION_JSON)
    Observable<GraphValidationError> checkConstraints() {
        log.trace "checkConstraints"

        List<GraphValidationError> constraintErrors = carnivalGraph
            .coreGraph
        .checkConstraints()
        constraintErrors.eachWithIndex { e, ei -> log.trace "${ei}: $e"}

        Observable.fromArray((GraphValidationError[])constraintErrors.toArray())
    }


    @Get("/") 
    @Produces(MediaType.TEXT_PLAIN) 
    HttpResponse<String> index() {
        def numVertices
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            numVertices = g.V().count().next()
        }
        
        HttpResponse.ok("There are $numVertices vertices in the graph.".toString())  
    }


    @Get("/vertices") 
    @Produces(MediaType.APPLICATION_JSON)
    HttpResponse<Observable<Map>> getAllVertices() {
        List<Vertex> verts = new ArrayList<Vertex>()
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            g.V().fill(verts)
        }
        log.trace "verts: ${verts}"

        List<Map> vertsData = verts.collect({ toMap((Vertex)it) })
        log.trace "vertsData: $vertsData"

        Map[] vertsDataArray = (Map[])vertsData.toArray()
        log.trace "vertsDataArray:${vertsDataArray}"  

        Observable data = Observable.fromArray(vertsDataArray)
        HttpResponse.ok(data).header("x-total-count", String.valueOf(vertsData.size()))
    }


    ///////////////////////////////////////////////////////////////////////////
    // HTTP POST
    ///////////////////////////////////////////////////////////////////////////

    @Post("/vertex")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Single<Map> postVertex(@Size(max = 1024) @Body String text) {
        log.trace "postVertex text:$text"
        assert text != null
        assert text.length() > 0

        // convert request body to JSON object
        def jsonSlurper = new JsonSlurper()
        Map params = (Map)jsonSlurper.parseText(text)
        log.trace "params:$params"
        assert params.vertexLabel != null
        
        // create the vertex
        Vertex vert
        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            vert = graph.addVertex(T.label, params.get('vertexLabel'))
        }
        assert vert != null

        // set vertex properties
        params.properties?.each { k, v ->
            vert.property(String.valueOf(k), v)
        }

        // return a map
        Map m = toMap(vert)
        Single.just((Map)m)
    }


}