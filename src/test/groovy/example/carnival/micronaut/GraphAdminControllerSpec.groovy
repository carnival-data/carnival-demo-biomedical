package example.carnival.micronaut


import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.http.MediaType

import spock.lang.Specification

import javax.inject.Inject



@MicronautTest 
class GraphAdminControllerSpec extends Specification {

    @Inject
    @Client("/")
    RxHttpClient client 

    void "test index response"() {
        when:
        HttpRequest request = HttpRequest.GET('/graphadmin') 
        String rsp = client.toBlocking().retrieve(request)

        then:
        rsp == "There are 7 vertices in the graph."
    }


}