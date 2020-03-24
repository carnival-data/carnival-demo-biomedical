package example.carnival.micronaut



import java.util.function.BiConsumer

import io.reactivex.Flowable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.http.MediaType

import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.Specification

import javax.inject.Inject

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph

import carnival.core.graph.Core



@MicronautTest 
class PersonControllerSpec extends Specification {


    ///////////////////////////////////////////////////////////////////////////
    // APPLICATION COMPONENTS
    ///////////////////////////////////////////////////////////////////////////

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client("/")
    RxHttpClient client 

    @Inject
    CarnivalGraph carnivalGraph


    ///////////////////////////////////////////////////////////////////////////
    // TEST LIFE CYCLE
    ///////////////////////////////////////////////////////////////////////////

    def setup() {
        carnivalGraph.resetCoreGraph()
    }


    ///////////////////////////////////////////////////////////////////////////
    // TESTS
    ///////////////////////////////////////////////////////////////////////////

    void "get by id"() {
        given:
        JsonSlurper jsonSlurper = new JsonSlurper()
        HttpRequest request
        String response
        def responseJsonObject

        when:
        Person p1 = client.retrieve(
            HttpRequest.PUT(
                '/person'
                , new Person(id:'58', name:'alex')
            ),
            Person.class
        ).blockingFirst()
        println "p1: $p1"
        
        Person p2 = client.retrieve(
            HttpRequest.GET('/person/58'),
            Person.class
        ).blockingFirst()
        println "p2: $p2"

        then:
        p2 != null
        p2.id == '58'
        p2.name == 'alex'
    }


    void "get by name"() {
        given:
        JsonSlurper jsonSlurper = new JsonSlurper()
        HttpRequest request
        String response
        def responseJsonObject

        with(embeddedServer) {
            println "host:${host} port:${port} scheme:${scheme} URI:${getURI()} URL:${getURL()}"
        }

        when:
        request = HttpRequest.PUT(
            '/person'
            , jsonSlurper.parseText('{"id":"58", "name":"alex"}')
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


    void "update"() {
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
            , jsonSlurper.parseText('{"id":"58", "name":"alex"}')
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


    void "cannot create same object twice"() {
        given:
        Exception e
        Person p = new Person(id:58, name:'alex')
        HttpRequest req =  HttpRequest.POST( '/person', p)    

        when:
        Person p1 = client.retrieve(req, Person.class).blockingFirst()

        then:
        p1 != null

        when:
        HttpResponse res = client.exchange(req).blockingFirst()

        then:
        e = thrown()
        e.status == HttpStatus.CONFLICT
    }


}