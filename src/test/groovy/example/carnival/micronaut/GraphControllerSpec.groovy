package example.carnival.micronaut


import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.http.MediaType

import io.reactivex.Flowable

import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph

import spock.lang.Specification

import javax.inject.Inject

import carnival.core.graph.Core
import carnival.graph.Base


@MicronautTest 
class GraphControllerSpec extends Specification {


    @Inject
    @Client("/")
    RxHttpClient client 

    @Inject
    CarnivalGraph carnivalGraph



    void "create vertex"() {
        given:
        String json = JsonOutput.toJson([vertexLabel:'Person', properties:[id:'58', name:'alex']])
        HttpRequest req =  HttpRequest.POST( '/graph/vertex', json)

        when:
        Map v1 = client.retrieve(req, Map.class).blockingFirst()

        then:
        v1 != null
        v1.vertexId != null
        v1.vertexLabel == 'Person'
        v1.propertyKeys != null
        v1.propertyKeys.contains('id')
        v1.propertyKeys.contains('name')
    }


    void "check constraints"() {
        when:
        // NAME is a required property of APPLICATION
        // otherwise, the below is correct
        carnivalGraph.coreGraph.graph.addVertex(
            T.label, Core.VX.APPLICATION.label,
            Base.PX.NAME_SPACE.label, Core.VX.APPLICATION.nameSpace
        )
        HttpRequest request = HttpRequest.GET('/graph/validation/check-constraints') 

        List<GraphController.ModelError> errors = client.jsonStream(
            request, 
            GraphController.ModelError.class
        ).blockingIterable().toList()
        println "errors: $errors"

        then:
        errors.size == 1
        errors[0].find { 
            it.message.contains('Property existence constraint') &&
            it.message.contains(Core.PX.NAME.label)
        }
    }


    void "check model"() {
        when:
        carnivalGraph.coreGraph.graph.addVertex(T.label, 'SomeUnmodelledThing')
        HttpRequest request = HttpRequest.GET('/graph/validation/check-model') 

        List<GraphController.ModelError> errors = client.jsonStream(
            request, 
            GraphController.ModelError.class
        ).blockingIterable().toList()
        println "errors: $errors"

        then:
        errors.size == 1
        errors[0].find { it.message.contains('SomeUnmodelledThing') }
    }


    void "all vertices"() {
        given:
        def numVertices
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            numVertices = g.V().count().next()
        }

        when:
        HttpRequest request = HttpRequest.GET('/graph/vertices') 

        List<Map> vdata = client.jsonStream(
            request, 
            Map.class
        ).blockingIterable().toList()
        println "vdata: $vdata"

        then:
        vdata.size == numVertices
    }


    void "index response"() {
        when:
        HttpRequest request = HttpRequest.GET('/graph') 
        String rsp = client.toBlocking().retrieve(request)

        then:
        rsp.contains("vertices in the graph")
    }


}