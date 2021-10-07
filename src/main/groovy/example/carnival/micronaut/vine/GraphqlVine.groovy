package example.carnival.micronaut.vine



import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Slf4j

import javax.inject.Singleton
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.text.DateFormat
import javax.inject.Inject

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.context.annotation.Property
import io.reactivex.FlowableSubscriber
import io.reactivex.Flowable

import org.reactivestreams.Subscription
import org.reactivestreams.Subscriber

import carnival.util.MappedDataTable
import carnival.core.vine.Vine
import carnival.core.vine.JsonVineMethod
import carnival.core.vine.CacheMode
import example.carnival.micronaut.config.AppConfig



@ToString(includeNames=true)
@Slf4j 
@Singleton
class GraphqlVine implements Vine {



    ///////////////////////////////////////////////////////////////////////////
    // SHARED CLASSES
    ///////////////////////////////////////////////////////////////////////////

    @ToString(includeNames=true)
    @EqualsAndHashCode(includes=['query'])
    static class QueryRequest {
        String query
    }


    @ToString(includeNames=true)
    static class QueryResponse {
        String account_id
    }



    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject AppConfig config

    @Inject @Client("https://api.monday.com/v2")
    RxStreamingHttpClient client 


    ///////////////////////////////////////////////////////////////////////////
    // CONVENIENCE METHODS
    ///////////////////////////////////////////////////////////////////////////

    String getApiToken() { config.monday.api.token }



    ///////////////////////////////////////////////////////////////////////////
    // Get USERS
    ///////////////////////////////////////////////////////////////////////////

    @ToString(includeNames=true)
    static class GetUsersResponse extends QueryResponse {

        @ToString(includeNames=true)
        static class UserData {

            @ToString(includeNames=true)
            static class User {
                Integer id
                String email
            }

            List<User> users
        }
        UserData data
    }


    @Slf4j
    class GetUsers extends JsonVineMethod<GetUsersResponse> {
		GetUsersResponse fetch(Map args = [:]) {
            log.trace "GetUsersResponse args:${args}"

            log.trace "getUsers args:${args}"
        
            QueryRequest body = new QueryRequest(
                query: "query { users() {id email} }"
            )
            log.trace "body: ${body}"
            
            HttpRequest req =  HttpRequest.POST('/', body)
            req.headers.add("Authorization", apiToken)

            GetUsersResponse response        
            client.exchange(req, GetUsersResponse.class)
                .blockingSubscribe(
                    { HttpResponse res ->
                        log.trace "res.status = ${res.status}"
                        response = res.body()
                    },
                    { Throwable t ->
                        log.error("getUsers", t)
                    }
            )

            response
        }
    }


}
