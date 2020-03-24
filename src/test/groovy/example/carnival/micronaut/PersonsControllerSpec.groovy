package example.carnival.micronaut



import java.util.function.BiConsumer

import io.reactivex.Flowable

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

import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.Specification

import javax.inject.Inject

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph

import carnival.core.graph.Core



@MicronautTest 
class PersonsControllerSpec extends Specification {


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

    void "test get all"() {
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
        ['alex', 'bob'].eachWithIndex { name, nameIdx ->
            client.toBlocking().retrieve(
                HttpRequest.PUT(
                    '/person'
                    , new Person(id:nameIdx, name:name)
                )
            )
        }

        List<Person> pps = new ArrayList<Person>()
        Flowable<Person> personStream = client.jsonStream(
            HttpRequest.GET('/persons'), Person.class
        )
        personStream.blockingForEach { Person p -> pps.add(p) }
        println "pps: $pps"

        then:
        pps.size() == 2
        pps.find { it.name == 'alex' }
        pps.find { it.name == 'bob' }
    }

}