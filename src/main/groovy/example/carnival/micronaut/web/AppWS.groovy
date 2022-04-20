package example.carnival.micronaut



import javax.inject.Inject
import javax.inject.Named
import javax.validation.constraints.Size
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.util.concurrent.Callable
import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.time.TimeCategory

import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.Flowable

import io.micronaut.core.annotation.Nullable
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Body
import io.micronaut.http.MediaType
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpHeaders
import io.micronaut.http.annotation.RequestAttribute
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.annotation.PathVariable

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.process.traversal.P

import carnival.core.graph.Core
import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.graph.CarnivalGraph
import example.carnival.micronaut.GraphModel



@Controller("/")
@Slf4j 
class AppWs {

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject AppConfig config
    @Inject CarnivalGraph carnivalGraph
    


    ///////////////////////////////////////////////////////////////////////////
    // WEB SERVICE METHODS
    ///////////////////////////////////////////////////////////////////////////


    @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    String home() {
        log.trace "home"   

        int numVertices
        int numEdges
        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            numVertices = g.V().count().next()
            numEdges = g.V().count().next()
        }     

        return """\
Carnival Micronaut Example Server

Config:
${config.name}
${config.subConfig.someNumber}
${config.subConfig.subSubConfig.anotherString}

Graph:
numVertices: ${numVertices}
numEdges: ${numEdges}
"""
    }



    @ToString(includeNames=true)
    static class CreateDoggieBody {
        Boolean isAdorable = true
        String name
    }

    @ToString(includeNames=true)
    static class CreateDoggieResponse {
        String message = ""
    }

    @Post(value = "/doggie/create", consumes = MediaType.APPLICATION_JSON)
    CreateDoggieResponse createDoggie(@Size(max = 1024) @Body CreateDoggieBody args) {
        log.trace "createDoggie args:$args"

        assert args.name
        
        def resp = new CreateDoggieResponse()
        
        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->

            def dV = GraphModel.VX.DOGGIE.instance()
                .withProperty(GraphModel.PX.IS_ADORABLE, args.isAdorable)
            .create(graph)

            def nV = GraphModel.VX.NAME.instance()
                .withProperty(GraphModel.PX.TEXT, args.name)
            .ensure(graph, g)

            def dnE = GraphModel.EX.HAS_BEEN_CALLED.instance()
                .from(dV)
                .to(nV)
            .create()

            resp.message = "created doggie ${dV}, name ${nV}, and name relationship ${dnE}"
        }

        resp
    }



    @Get("/doggies")
    @Produces(MediaType.TEXT_PLAIN)
    String doggies(
        @Nullable @QueryValue Boolean isAdorable,
        @Nullable @QueryValue String name
    ) {

        log.trace "doggies isAdorable:${isAdorable} name:${name}"

        String response = ""

        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            Integer numDoggies = g.V()
                .isa(GraphModel.VX.DOGGIE)
            .count().next()
            response += "There are ${numDoggies} total doggies."

            g.V()
                .isa(GraphModel.VX.DOGGIE).as('d')
                .out(GraphModel.EX.HAS_BEEN_CALLED)
                .isa(GraphModel.VX.NAME).as('n')
                .select('d', 'n')
            .each { m ->
                response += "\n${m.d} ${GraphModel.PX.TEXT.valueOf(m.n)}"
            }

            if (isAdorable != null) {
                Integer numAdorableDoggies = g.V()
                    .isa(GraphModel.VX.DOGGIE)
                    .has(GraphModel.PX.IS_ADORABLE, isAdorable)
                .count().next()
                response += "\nThere are ${numAdorableDoggies} doggies that are"
                if (!isAdorable) response += " not"
                response += " adorable."      
            }
        }
        
        response
    }

//    @Get(value = "/patient/create")
//    CreateDoggieResponse createPatient() {
    @Post(value = "/patient/create", consumes = MediaType.APPLICATION_JSON)
    CreateDoggieResponse createPatient(@Size(max = 1024) @Body CreateDoggieBody args) {
//        log.trace "createDoggie args:$args"

//        assert args.name

        def resp = new CreateDoggieResponse()

        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->

            def dV = GraphModel.VX.PATIENT.instance()
                .withProperty(GraphModel.PX.ID, "123")
                .create(graph)

//            def nV = GraphModel.VX.NAME.instance()
//                    .withProperty(GraphModel.PX.TEXT, args.name)
//                    .ensure(graph, g)
//
//            def dnE = GraphModel.EX.HAS_BEEN_CALLED.instance()
//                    .from(dV)
//                    .to(nV)
//                    .create()

            resp.message = "created patient ${dV}"
        }

        resp
    }

    @Get("/patients")
    @Produces(MediaType.TEXT_PLAIN)
    String patients() {

        String response = ""

        carnivalGraph.coreGraph.withTraversal { Graph graph, GraphTraversalSource g ->
            Integer numPatients = g.V()
                .isa(GraphModel.VX.PATIENT)
                .count().next()
            response += "There are ${numPatients} total patients."

           g.V()
                   .isa(GraphModel.VX.PATIENT).as('p')
                   .has(GraphModel.PX_PATIENT.AGE, P.between(18, 55))
                   .out(GraphModel.EX.HAS)
                   .isa(GraphModel.VX.ENCOUNTER).as('e')
                   .select('p', 'e')
                   .each { m ->
                       response += "\n${m.p} ${m.e} ${GraphModel.PX_PATIENT.AGE.valueOf(m.p)}"
                   }

           /*if (isAdorable != null) {
               Integer numAdorableDoggies = g.V()
                       .isa(GraphModel.VX.DOGGIE)
                       .has(GraphModel.PX.IS_ADORABLE, isAdorable)
                       .count().next()
               response += "\nThere are ${numAdorableDoggies} doggies that are"
               if (!isAdorable) response += " not"
               response += " adorable."
           }*/
        }

        response
    }
}