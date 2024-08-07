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
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.http.server.types.files.SystemFile

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerVertex

import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.T

import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.graph.BiomedCarnival
import example.carnival.micronaut.GraphModel



@Controller("/")
@Slf4j 
class AppWs {

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject AppConfig config
    @Inject BiomedCarnival carnivalGraph
    static String baseUrl = "http://localhost:5858"


    ///////////////////////////////////////////////////////////////////////////
    // WEB SERVICE METHODS
    ///////////////////////////////////////////////////////////////////////////


    @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    String home() {
        log.trace "home"   

        int numVertices
        int numEdges
        int numPatients
        int numEncounters
        int numConditions
        int numMedications
        int numSurveyResponses

        carnivalGraph.carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            numVertices = g.V().count().next()
            numEdges = g.E().count().next()

            numPatients = g.V().isa(GraphModel.VX.PATIENT).count().next()
            numEncounters = g.V().isa(GraphModel.VX.ENCOUNTER).count().next()
            numConditions = g.V().isa(GraphModel.VX.CONDITION).count().next()
            numMedications = g.V().isa(GraphModel.VX.MEDICATION).count().next()
            numSurveyResponses = g.V().isa(GraphModel.VX.SURVEY).count().next()
        }     

        return """\
Carnival Micronaut Example Server

Config:
${config.name}

Graph:
Total Number of Vertices: ${numVertices}
Total Number of Edges: ${numEdges}

Total Number of Patients: ${numPatients}
Total Number of Encounters: ${numEncounters}
Total Number of Conditions: ${numConditions}
Total Number of Medications: ${numMedications}
Total Number of Survey Question Responses: ${numSurveyResponses}
"""
    }

    class Patient {
        String id = ""
        String url = ""
        String first_name = ""
        String last_name = ""
        List<String> encounter_urls = []

        Map diagnosed_with = [:]
        Map perscribed = [:]
    }
    class PatientResponse {
        List<Patient> patients = []
    }

    class Encounter {
        String id = ""
        String url = ""
        String patient_url = ""

        String start = ""
        String end = ""

        String encounter_class = ""
        String code = ""
        String description = ""

        String reason_code = ""
        String reason_description = ""

        Map diagnosed_at = [:]
        Map perscribed_at = [:]
    }

    private Encounter parseEncounterFromVertex(TinkerVertex eVtx) {
        Encounter e = new Encounter()
        e.id = GraphModel.PX.ID.valueOf(eVtx)
        e.url = baseUrl + "/encounter/" + GraphModel.PX.ID.valueOf(eVtx)

        e.start = GraphModel.PX.START.valueOf(eVtx)
        e.end = GraphModel.PX.END.valueOf(eVtx)

        e.encounter_class = GraphModel.PX_ENCOUNTER.CLASS.valueOf(eVtx)
        e.code = GraphModel.PX.CODE.valueOf(eVtx)
        e.description = GraphModel.PX.DESCRIPTION.valueOf(eVtx)

        e.reason_code = GraphModel.PX_ENCOUNTER.REASON_CODE.valueOf(eVtx)
        e.reason_description = GraphModel.PX_ENCOUNTER.REASON_DESCRIPTION.valueOf(eVtx)

        carnivalGraph.carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            e.patient_url = baseUrl + "/patient/" +
                g.V(eVtx).in(GraphModel.EX.HAS).isa(GraphModel.VX.PATIENT)
                .values(GraphModel.PX.ID.getLabel()).toList().first()

            e.diagnosed_at["condition_codes"] = g.V(eVtx).out(GraphModel.EX.DIAGNOSED_AT)
                .values(GraphModel.PX.CODE.getLabel())
                .dedup()
                .toList()

            e.perscribed_at["medication_codes"] = g.V(eVtx).out(GraphModel.EX.PRESCRIBED_AT)
                .values(GraphModel.PX.CODE.getLabel())
                .dedup()
                .toList()
        }

        return e
    
    }

    private Patient parsePatientFromVertex(TinkerVertex pVtx) {
        Patient p = new Patient()
        p.id = GraphModel.PX.ID.valueOf(pVtx)
        //p.url = "<a href = '" + baseUrl + "/patient/" + GraphModel.PX.ID.valueOf(pVtx) + "'>foo</a>"
        p.url = baseUrl + "/patient/" + GraphModel.PX.ID.valueOf(pVtx)

        p.first_name = GraphModel.PX_PATIENT.FIRST_NAME.valueOf(pVtx)
        p.last_name = GraphModel.PX_PATIENT.LAST_NAME.valueOf(pVtx)

        carnivalGraph.carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            p.encounter_urls = g.V(pVtx).out(GraphModel.EX.HAS).isa(GraphModel.VX.ENCOUNTER)
                .values(GraphModel.PX.ID.getLabel())
                .dedup()
                .toList()
                .collect{ baseUrl + "/encounter/$it" }

            p.diagnosed_with["condition_codes"] = g.V(pVtx).out(GraphModel.EX.DIAGNOSED_WITH)
                .values(GraphModel.PX.CODE.getLabel())
                .dedup()
                .toList()

            p.perscribed["medication_codes"] = g.V(pVtx).out(GraphModel.EX.PRESCRIBED)
                .values(GraphModel.PX.CODE.getLabel())
                .dedup()
                .toList()
        }

        return p
    }


    @Get("/export/graphml")
    SystemFile exportGraphml () {
        File file = new File("carnival-micronaut-home/export/patient_graph.graphml")
        return new SystemFile(file).attach("carnival-micronaut-home/export/patient_graph.graphml");
    }

    @Get("/export/graphson")
    SystemFile exportGraphson () {
        File file = new File("carnival-micronaut-home/export/patient_graph-graphson.json")
        return new SystemFile(file).attach("carnival-micronaut-home/export/patient_graph-graphson.json");
    }


    @Get("/patient/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Patient patient(String id) {
        def response = new Patient()
        carnivalGraph.carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            def patVs = g.V().isa(GraphModel.VX.PATIENT).has(GraphModel.PX.ID, id).toList()
           
            if(patVs) response = parsePatientFromVertex(patVs.first())
            else throw new HttpStatusException(HttpStatus.NOT_FOUND, "Patient not found")
        }
        response
    }

    @Get("/encounter/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Encounter encounter(String id) {
        def response = new Encounter()
        carnivalGraph.carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            def patVs = g.V().isa(GraphModel.VX.ENCOUNTER).has(GraphModel.PX.ID, id).toList()
           
            if(patVs) response = parseEncounterFromVertex(patVs.first())
            else throw new HttpStatusException(HttpStatus.NOT_FOUND, "Encounter not found")
        }
        response
    }

    @Get("/case_patients")
    @Produces(MediaType.APPLICATION_JSON)
    PatientResponse casePatients() {
        def response = new PatientResponse()
        carnivalGraph.carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            def patientVs = g.V()
                .isa(GraphModel.VX.COHORT_PATIENTS).as('anw')
                .out(GraphModel.EX.HAS)
                .isa(GraphModel.VX.PATIENT).as('p')
                .select('p')
                .each { m ->
                    Patient p = parsePatientFromVertex(m)
                    response.patients << p   
                    //response += "\n ${p.id}" 
                }
        }
        response
    }

    @Get("/control_patients")
    @Produces(MediaType.APPLICATION_JSON)
    PatientResponse controlPatients() {
        def response = new PatientResponse()
        carnivalGraph.carnival.withTraversal { Graph graph, GraphTraversalSource g ->
            def patientVs = g.V()
                    .isa(GraphModel.VX.CONTROL_PATIENTS).as('anw')
                    .out(GraphModel.EX.HAS)
                    .isa(GraphModel.VX.PATIENT)
                    .as('p')
                    .select('p')
                    .each { m ->

                        Patient p = parsePatientFromVertex(m)
                        response.patients << p
                        //response += "\n ${p.id}"
                    }
        }
        response
    }
}