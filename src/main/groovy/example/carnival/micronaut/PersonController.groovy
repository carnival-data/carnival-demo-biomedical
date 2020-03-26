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



/** Controller for Person object */
@CompileStatic
@Controller("/person") 
class PersonController {

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


    @Get("/{personId}") 
    @Produces(MediaType.APPLICATION_JSON)
    Single<Person> getPerson(Long personId) {
        log.trace "getPerson personId:$personId"

        Vertex personV
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            personV = g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
                .has(GraphModel.PX.ID.label, String.valueOf(personId))
            .next()
        }
        log.trace "personV: ${personV}"

        Single.just(Person.create(personV))
    }


    @Get("/") 
    @Produces(MediaType.APPLICATION_JSON)
    Single<Person> getPersonByName(@QueryValue('name') String name) {
        Vertex personV
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            personV = g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
                .has(Core.PX.NAME.label, name)
            .next()
        }
        log.trace "personV: ${personV}"

        Single.just(Person.create(personV))
    }



    @Put("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Single<Person> putPerson(@Body Person person) {
        log.trace "putPerson json:$person"

        assert person != null
        assert person.id != null
        assert person.name != null
        
        Vertex personV

        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            personV = GraphModel.VX.PERSON.instance()
                .withProperties(
                    GraphModel.PX.ID, String.valueOf(person.id),
                    Core.PX.NAME, person.name
                )
            .vertex(graph, g)
        }

        Single.just(Person.create(personV))
    }


    @Post("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Single<Person> postPerson(@Body Person person) {
        log.trace "postPerson json:$person"
        assert person.name != null
        assert person.id != null
        
        boolean isPresent = false
        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            isPresent = GraphModel.VX.PERSON.instance()
                .withProperties(
                    GraphModel.PX.ID, String.valueOf(person.id),
                    Core.PX.NAME, person.name
                )
            .traversal(graph, g).tryNext().isPresent()
        }

        // these probably aren't what you want to do
        //if (isPresent) return HttpResponse.status(HttpStatus.CONFLICT, "Entity already exists: ${person}")
        //if (isPresent) return Single.error(new Exception("Entity already exists: ${person}"))

        // throw an exception for which we have coded an explicit handler,
        // EntityExistsExceptionHandler, which returns an HTTP status 409
        if (isPresent) throw new EntityExistsException("Entity already exists: ${person}")

        Vertex personV
        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            personV = GraphModel.VX.PERSON.instance()
                .withProperties(
                    GraphModel.PX.ID, String.valueOf(person.id),
                    Core.PX.NAME, person.name
                )
            .vertex(graph, g)
        }

        Single.just(Person.create(personV))
    }


}


///////////////////////////////////////////////////////////////////////////////
// PLAYGROUND
///////////////////////////////////////////////////////////////////////////////
/*
//import com.fasterxml.jackson.annotation.JsonIgnore


//ObservableSource
//subscribe(Observer<? super T> observer)

//Observable
//onComplete()
//onError(Throwable e)
//onNext(T t)
//onSubscribe(Disposable d)

//Disposable
//dispose()
//isDisposed()

    static class ObservableTraversal implements ObservableSource<Person> {
        @JsonIgnore
        GraphTraversalSource g

        @JsonIgnore
        Traversal traversal

        final String something = 'something'

        ObservableTraversal(GraphTraversalSource g, Traversal traversal) {
            this.g = g
            this.traversal = traversal
        }
        
        void subscribe(io.reactivex.Observer<Person> observer) {
            try {
                Optional val = traversal.tryNext()
                while (!val.equals(Optional.empty())) {
                    observer.onNext(Person.create((Vertex)val.get()))
                    val = traversal.tryNext()
                }
                //traversal.forEachRemaining { Vertex v ->
                //    observer.onNext(Person.create((Vertex)v)) 
                //}
            } catch (Throwable t) {
                observer.onError(t)
            }
            observer.onComplete()
            g.close()
        }
    }


    ObservableSource<Person> list() {
        def g = carnivalGraph.coreGraph.traversal()
        def trav = g.V().hasLabel(GraphModel.VX.PERSON.label)
        new ObservableTraversal(g, trav)
    }


*/