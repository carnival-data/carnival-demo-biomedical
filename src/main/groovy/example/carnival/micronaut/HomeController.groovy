package example.carnival.micronaut



import javax.inject.Inject

import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable

import groovy.transform.CompileStatic
import groovy.transform.ToString

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Body
import io.micronaut.http.MediaType
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.RequestAttribute
import io.micronaut.http.annotation.QueryValue

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Vertex

import carnival.core.graph.Core



/** Home (root) controller */
@CompileStatic
@Controller("/") 
class HomeController {

    ///////////////////////////////////////////////////////////////////////////
    // STATIC
    ///////////////////////////////////////////////////////////////////////////

    /** */
    static Logger log = LoggerFactory.getLogger(HomeController.class)



    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    /** */
    @Inject
    CarnivalGraph carnivalGraph
    

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////


    @Get("/") 
    @Produces(MediaType.APPLICATION_JSON)
    HttpResponse<String> getHome() {
        HttpResponse.ok('carnival-micronaut')
    }
    

}
