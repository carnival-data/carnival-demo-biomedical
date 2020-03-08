package example.carnival.micronaut



import java.util.function.BiConsumer

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.http.MediaType

import spock.lang.Specification

import javax.inject.Inject

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph

import carnival.core.graph.Core



@MicronautTest 
class PersonControllerSpec extends Specification {


    @Inject
    ApplicationContext ctx

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client("/")
    RxHttpClient client 

    @Inject
    CarnivalGraph carnivalGraph

    @Property(name = "some.test.property")
    String someTestProperty


    void "test external property"() {
        expect:
        ctx.environment.getProperty("external.test.property", String).get() == 'super'
    }


    void "test property config annotation"() {
        expect:
        someTestProperty == 'yay'
    }


    void "test person get by name"() {
        given:
        JsonSlurper jsonSlurper = new JsonSlurper()
        HttpRequest request
        String response
        def responseJsonObject

        with(embeddedServer) {
            println "host:${host} port:${port} scheme:${scheme} URI:${getURI()} URL:${getURL()}"
        }

        expect:
        ctx != null

        when:
        request = HttpRequest.PUT(
            '/person'
            , jsonSlurper.parseText('{"name":"alex"}')
        )
        String rsp = client.toBlocking().retrieve(request)

        then:
        rsp != null

        when:
        def personM1 = jsonSlurper.parseText(rsp)

        then:
        personM1 != null
        personM1 instanceof Map
        personM1.label == GraphModel.VX.PERSON.label
        personM1.name == 'alex'

        when:
        request = HttpRequest.GET('/person?name=alex')
        println "request: ${request.metaClass.methods}"
        println "parameters: ${request.parameters.metaClass.methods}"

        // this works too
        //request.parameters.add('name', 'alex')
        //request.parameters.forEach ({ String k, List v ->
        //    println "param $k $v"
        //} as BiConsumer)
        //println "parameters: ${request.parameters.values}"

        // use .exchange instead of .retrieve to check statuses or other
        // response wrapper stuff. .retrieve just gets the body.

        response = client.toBlocking().retrieve(request)
        println "response: $response"

        def personM2 = jsonSlurper.parseText(response)
        println "personM2: ${personM2.class} ${personM2}"

        then:
        personM2 != null
        personM2 instanceof Map
        personM2.label == personM1.label
        personM2.name == personM1.name
        personM2.id == personM1.id
    }



    void "test person list"() {
        given:
        JsonSlurper jsonSlurper = new JsonSlurper()
        HttpRequest request
        String response
        def responseJsonObject

        when:
        def np
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            np = g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
            .count().next()
        }

        then:
        np == 0

        when:
        request = HttpRequest.PUT(
            '/person'
            , jsonSlurper.parseText('{"name":"alex"}')
        )
        String rsp = client.toBlocking().retrieve(request)

        then:
        rsp != null

        when:
        def rspo = jsonSlurper.parseText(rsp)

        then:
        rspo != null
        rspo instanceof Map
        rspo.label == GraphModel.VX.PERSON.label
        rspo.name == 'alex'

        when:
        request = HttpRequest.GET('/person/list')
        response = client.toBlocking().retrieve(request)
        println "response: $response"

        responseJsonObject = jsonSlurper.parseText(response)
        println "responseJsonObject: ${responseJsonObject.class} ${responseJsonObject}"

        then:
        responseJsonObject != null
        responseJsonObject instanceof List
        responseJsonObject.size() == 1
        responseJsonObject[0].label == GraphModel.VX.PERSON.label
        responseJsonObject[0].name == 'alex'
    }



    void "test person put"() {
        given:
        JsonSlurper jsonSlurper = new JsonSlurper()

        when:
        def np1
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            np1 = g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
                .has(Core.PX.NAME.label, 'alex')
            .count().next()
        }

        then:
        np1 == 0        

        when:
        HttpRequest request = HttpRequest.PUT(
            '/person'
            , jsonSlurper.parseText('{"name":"alex"}')
        )
            //.contentType(MediaType.APPLICATION_JSON)
            //.accept(MediaType.TEXT_JSON) 
        String rsp = client.toBlocking().retrieve(request)

        then:
        rsp != null

        when:
        def rspo = jsonSlurper.parseText(rsp)

        then:
        rspo != null
        rspo instanceof Map
        rspo.label == GraphModel.VX.PERSON.label
        rspo.name == 'alex'

        when:
        def np2
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            np2 = g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
                .has(Core.PX.NAME.label, 'alex')
            .count().next()
        }

        then:
        np2 == np1 + 1
    }

}