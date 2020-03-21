package example.carnival.micronaut



import javax.inject.Inject

import io.reactivex.Single
import io.reactivex.Observable

import groovy.transform.CompileStatic
import groovy.json.JsonOutput
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

    @Get("/description") 
    @Produces(MediaType.TEXT_PLAIN) 
    String index() {
        def numPeople
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            numPeople = g.V().hasLabel(GraphModel.VX.PERSON.label).count().next()
        }

        "There are $numPeople people in the graph." 
    }


    @Get("/list") 
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

        Observable.fromArray(ppsa)// as Observable<Person>
    }



    
    @Get("/") 
    @Produces(MediaType.TEXT_JSON)
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
    @Produces(MediaType.TEXT_JSON)
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
                    GraphModel.PX.ID, person.id,
                    Core.PX.NAME, person.name
                )
            .vertex(graph, g)
        }

        Single.just(Person.create(personV))
    }


    @Post("/")
    @Produces(MediaType.TEXT_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Single<Person> postPerson(@Body Person person) {
        log.trace "postPerson json:$person"
        assert person.name != null
        assert person.id != null
        
        Vertex personV

        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            personV = GraphModel.VX.PERSON.instance()
                .withProperties(
                    GraphModel.PX.ID, person.id,
                    Core.PX.NAME, person.name
                )
            .vertex(graph, g)
        }

        Single.just(Person.create(personV))
    }


}