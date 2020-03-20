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
class ExternalConfigSpec extends Specification {


    ///////////////////////////////////////////////////////////////////////////
    // APPLICATION COMPONENTS
    ///////////////////////////////////////////////////////////////////////////

    @Inject
    ApplicationContext ctx

    @Property(name = "some.test.property")
    String someTestProperty



    ///////////////////////////////////////////////////////////////////////////
    // TESTS
    ///////////////////////////////////////////////////////////////////////////

    /**
    void "test external property"() {
        expect:
        ctx
        ctx.environment.getProperty("external.test.property", String).get() == 'super'
    }


    void "test property config annotation"() {
        expect:
        ctx
        someTestProperty == 'yay'
    }
    **/

    void "not a test"() {
        expect:
        1 == 1
    }

}