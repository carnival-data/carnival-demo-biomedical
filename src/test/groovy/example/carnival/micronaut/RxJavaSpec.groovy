package example.carnival.micronaut



import java.util.function.BiConsumer

import io.reactivex.Single
import io.reactivex.Observable

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



class RxJavaSpec extends Specification {


    ///////////////////////////////////////////////////////////////////////////
    // TESTS
    ///////////////////////////////////////////////////////////////////////////

    void "hello world test"() {
        when:
        def result
        Observable<String> observable = Observable.just("Hello");
        observable.subscribe{String s -> result = s};
        
        then:
        result.equals("Hello")
    }

    void "observable fromArray single element"() {
        when:
        String[] words = ['i', 'hate', 'computers'].toArray()
        Observable<String> obs = Observable.fromArray(words)
        List<String> emittedWords = new ArrayList<String>()
        obs.blockingForEach { String w -> emittedWords.add(w) }
        //Thread.sleep(1000)

        then:
        emittedWords.size() == 3

    }

}