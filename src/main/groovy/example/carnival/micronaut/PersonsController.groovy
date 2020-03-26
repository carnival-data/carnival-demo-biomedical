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
import io.micronaut.http.annotation.RequestAttribute
import io.micronaut.http.annotation.QueryValue

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Vertex

import carnival.core.graph.Core



/** Controller for Person object */
@CompileStatic
@Controller("/persons") 
class PersonsController {

    ///////////////////////////////////////////////////////////////////////////
    // STATIC
    ///////////////////////////////////////////////////////////////////////////

    /** */
    static Logger log = LoggerFactory.getLogger(PersonController.class)



    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    /** */
    @Inject
    CarnivalGraph carnivalGraph
    
    // this is an alternative to the @Inject mechanism
    //protected final CarnivalGraph carnivalGraph
    //PersonController(CarnivalGraph carnivalGraph) {
    //    this.carnivalGraph = carnivalGraph
    //}


    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    @Get("/") 
    @Produces(MediaType.TEXT_JSON)
    Observable<Person> list() {
        List<Vertex> personVs = new ArrayList<Vertex>()
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
            .fill(personVs)
        }
        log.trace "personVs: ${personVs}"

        List<Person> pps = personVs.collect({ Person.create((Vertex)it) })
        log.trace "pps: $pps"

        Person[] ppsa = (Person[])pps.toArray()
        log.trace "ppsa:${ppsa}"  

        Observable.fromArray(ppsa)
    }

    
    @Get("/overview") 
    @Produces(MediaType.TEXT_PLAIN) 
    Single<String> index() {
        def numPeople
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            numPeople = g.V().hasLabel(GraphModel.VX.PERSON.label).count().next()
        }

        Single.just("There are $numPeople people in the graph.".toString())
    }





}
