package example.carnival.micronaut



import javax.inject.Inject

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



/** A POJO representing a Person */
@ToString(includeNames=true)
class Person {
    static Person create(Vertex v) {
        Person p = new Person()
        p.id = v.id()
        p.label = v.label()
        p.name = Core.PX.NAME.valueOf(v)
        p
    }
    String label = GraphModel.VX.PERSON.label
    int id
    String name
}



/** Controller for Person object */
@CompileStatic
@Controller("/person") 
class PersonController {

    /** */
    static Logger log = LoggerFactory.getLogger(PersonController.class)

    /** */
    @Inject
    CarnivalGraph carnivalGraph
    
    // this is an alternative to the @Inject mechanism
    //protected final CarnivalGraph carnivalGraph
    //PersonController(CarnivalGraph carnivalGraph) {
    //    this.carnivalGraph = carnivalGraph
    //}


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
    HttpResponse list() {
        def personVs = []
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
            .fill(personVs)
        }
        println "personVs: ${personVs}"

        def jso = personVs.collect { Person.create((Vertex)it) }
        println "jso:${jso}"

        HttpResponse.ok().body(jso)
    }


    
    @Get("/") 
    @Produces(MediaType.TEXT_JSON)
    HttpResponse<Person> getPersonByName(@QueryValue('name') String name) {
        Vertex personV
        carnivalGraph.coreGraph.withTraversal { GraphTraversalSource g ->
            personV = g.V()
                .hasLabel(GraphModel.VX.PERSON.label)
                .has(Core.PX.NAME.label, name)
            .next()
        }
        println "personV: ${personV}"

        HttpResponse.ok().body(Person.create(personV))
    }



    @Put("/")
    @Produces(MediaType.TEXT_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    HttpResponse<Person> putPerson(@Body Person person) {
        log.trace "putPerson json:$person"
        
        Vertex personV

        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            personV = GraphModel.VX.PERSON.instance()
                .withProperty(Core.PX.NAME, person.name)
            .vertex(graph, g)
        }

        /*
        HttpResponse.ok().body(JsonOutput.toJson(
            [label: personV.label(), name: Core.PX.NAME.valueOf(personV)]
        ))
        */

        Person p = Person.create(personV)
        HttpResponse.ok(p)

    }

}