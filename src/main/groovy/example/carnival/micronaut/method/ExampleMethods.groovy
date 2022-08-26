package example.carnival.micronaut.method

import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.apache.tinkerpop.gremlin.structure.Vertex

import javax.inject.Singleton
import javax.inject.Inject
import java.text.*
import java.time.Period
import java.time.LocalDate
import java.time.ZoneId

import java.text.*
import java.time.LocalDateTime
import java.time.Period
import java.time.LocalDate
import java.time.ZoneId


import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import carnival.core.graph.GraphMethods
import carnival.core.graph.GraphMethod
//import carnival.graph.ext.TinkerpopAnonTraversalExtension
//import carnival.graph.ext.TinkerpopTraversalExtension


import example.carnival.micronaut.GraphModel
import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.vine.ExampleDbVine
import example.carnival.micronaut.graph.CarnivalGraph
import carnival.util.DataTable
import java.text.SimpleDateFormat
import java.text.DateFormat
import java.time.Period
import java.time.ZoneId
import java.time.*


import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__

@ToString(includeNames=true)
@Slf4j 
@Singleton
class ExampleMethods implements GraphMethods { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject AppConfig config
    @Inject ExampleDbVine exampleDbVine
    @Inject CarnivalGraph carnivalGraph

    static Map<String, Vertex> patient_cache = new HashMap<String, Vertex>()
    static Map<String, Vertex> encounter_cache = new HashMap<String, Vertex>()

    ///////////////////////////////////////////////////////////////////////////
    // SERVICE METHOD
    ///////////////////////////////////////////////////////////////////////////

    class LoadPatients extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def mdt = exampleDbVine
                .method('Patients')
                .call()
                .result

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd")

            mdt.data.values().each { rec ->

                def age = Period.between(
                        formatter.parse(rec.BIRTH_DATE)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(), LocalDate.now()).getYears()

                String patient_id = rec.ID

                def patient_vertex = GraphModel.VX.PATIENT.instance().withProperties(
                    GraphModel.PX.ID, rec.ID,
                    GraphModel.PX_PATIENT.BIRTH_DATE, rec.BIRTH_DATE,
                    GraphModel.PX_PATIENT.FIRST_NAME, rec.FIRST_NAME,
                    GraphModel.PX_PATIENT.LAST_NAME, rec.LAST_NAME,
                    GraphModel.PX_PATIENT.LATITUDE, rec.LATITUDE,
                    GraphModel.PX_PATIENT.LONGITUDE, rec.LONGITUDE,
                    GraphModel.PX_PATIENT.AGE, age
                )

                patient_cache.put(patient_id, patient_vertex.create(graph))

            }

        }

    }

    /** */
    class LoadEncounters extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def mdt = exampleDbVine
                .method('Encounters')
                .call()
            .result

            mdt.data.values().each { rec ->
                log.trace "rec: ${rec}"

                def encV = GraphModel.VX.ENCOUNTER.instance().withProperties(
                        GraphModel.PX.ID, rec.ENCOUNTER_ID,
                        GraphModel.PX.START, rec.START,
                        GraphModel.PX.END, rec.STOP
                ).create(graph)

                encounter_cache.put(rec.ENCOUNTER_ID, encV)

                String patient_id = rec.PATIENT_ID

                def patient_vertex

                if (patient_cache.containsKey(patient_id)) {
                    patient_vertex = patient_cache.get(patient_id)
                }

                else {
                    patient_vertex = g.V().isa(GraphModel.VX.PATIENT).has(GraphModel.PX.ID, patient_id).next()
                    patient_cache.put(patient_id, patient_vertex)
                }

                GraphModel.EX.HAS.instance().from(patient_vertex).to(encV).create()
            }
        }
    }

    /** */
    class LoadConditions extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def gdt = exampleDbVine
                .method('Conditions')
                .call()
                .result

            gdt.dataIterator().each { rec ->
                log.trace "rec: ${rec}"
                def conditionV = GraphModel.VX.CONDITION.instance()
                    .withProperty(GraphModel.PX.START, rec.START)
                    .withNonNullProperties(GraphModel.PX.END, rec.END)
                    .withProperty(GraphModel.PX.CODE, rec.CODE)
                    .withProperty(GraphModel.PX.DESCRIPTION, rec.DESCRIPTION)
                    .create(graph)

                String patient_id = rec.PATIENT_ID
                String encounter_id = rec.ENCOUNTER_ID

                Vertex patient_vertex
                Vertex encounter_vertex

                if (patient_cache.containsKey(patient_id)) {
                    patient_vertex = patient_cache.get(patient_id)
                }
                else {
                patient_vertex = g.V().isa(GraphModel.VX.PATIENT).has(GraphModel.PX.ID, patient_id).next()
                    patient_cache.put(patient_id, patient_vertex)
                }

                if (encounter_cache.containsKey(encounter_id)) {
                    encounter_vertex = encounter_cache.get(encounter_id)
                }
                else {
                    encounter_vertex = g.V().isa(GraphModel.VX.ENCOUNTER).has(GraphModel.PX.ID, encounter_id).next()
                    encounter_cache.put(encounter_id, encounter_vertex)
                }

                GraphModel.EX.DIAGNOSED_WITH.instance().from(patient_vertex).to(conditionV).create()
                GraphModel.EX.DIAGNOSED_AT.instance().from(encounter_vertex).to(conditionV).create()

            }
        }
    }
    

    class LoadMedications extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def gdt = exampleDbVine
                .method('Medications')
                .call()
            .result

            gdt.dataIterator().each { rec ->
                log.trace "rec: ${rec}"
                def medV = GraphModel.VX.MEDICATION.instance()
                    .withProperty(GraphModel.PX.START, rec.START)
                    //GraphModel.PX.END, rec.STOP,
                    .withProperty(GraphModel.PX_MEDICATION.COST, rec.BASE_COST)
                    .withProperty(GraphModel.PX_MEDICATION.DISPENSES, rec.DISPENSES)
                    .withProperty(GraphModel.PX_MEDICATION.TOTAL_COST, rec.TOTAL_COST)
                    .withProperty(GraphModel.PX.CODE, rec.CODE)
                    .withProperty(GraphModel.PX.DESCRIPTION, rec.DESCRIPTION)
                    .withNonNullProperties(GraphModel.PX_MEDICATION.REASON_CODE, rec.REASON_CODE)
                    .withNonNullProperties(GraphModel.PX_MEDICATION.REASON_DESCRIPTION, rec.REASON_DESCRIPTION)
//                .ensure(graph, g)
                .create(graph)

                def patient_id = rec.PATIENT_ID
                def encounter_id = rec.ENCOUNTER_ID

                def patient_vertex
                def encounter_vertex

                if (patient_cache.containsKey(patient_id)) {
                    patient_vertex = patient_cache.get(patient_id)
                }
                else {
                    patient_vertex = g.V().isa(GraphModel.VX.PATIENT).has(GraphModel.PX.ID, patient_id).next()
                    patient_cache.put(patient_id, patient_vertex)
                }

                if (encounter_cache.containsKey(encounter_id)) {
                    encounter_vertex = encounter_cache.get(encounter_id)
                }
                else {
                    encounter_vertex = g.V().isa(GraphModel.VX.ENCOUNTER).has(GraphModel.PX.ID, encounter_id).next()
                    encounter_cache.put(encounter_id, encounter_vertex)
                }

                GraphModel.EX.PRESCRIBED.instance().from(patient_vertex).to(medV).create()
                GraphModel.EX.PRESCRIBED_AT.instance().from(encounter_vertex).to(medV).create()
            }
        }
    }

    class LoadSurveys extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def dt = DataTable.readDataFromCsvFile(
                    "data" + File.separator + "survey" + File.separator + "observations_survey.csv")

            dt.each() { rec->

                log.trace "rec: ${rec}"
                def surveyVBuilder = GraphModel.VX.SURVEY.instance().withProperties(
                        GraphModel.PX_SURVEY.DATE, rec.DATE,
                        GraphModel.PX.CODE, rec.CODE,
                        GraphModel.PX.DESCRIPTION, rec.DESCRIPTION
                )
                if (rec.TYPE == "text") {
                    surveyVBuilder = GraphModel.VX.SURVEY.instance().withProperty(
                            GraphModel.PX_SURVEY.RESPONSE_TEXT, rec.VALUE
                    )
                } else if (rec.TYPE == "numeric") {
                    surveyVBuilder = GraphModel.VX.SURVEY.instance()
                            .withProperty(GraphModel.PX_SURVEY.RESPONSE_NUMERIC, rec.VALUE)
                            .withNonNullProperties(GraphModel.PX_SURVEY.RESPONSE_UNIT, rec.UNITS)
                }
                def surveyV = surveyVBuilder.create(graph)

                def patient_id = rec.PATIENT

                def patient_vertex

                if (patient_cache.containsKey(patient_id)) {
                    patient_vertex = patient_cache.get(patient_id)
                }

                else {
                    patient_vertex = g.V().isa(GraphModel.VX.PATIENT).has(GraphModel.PX.ID, patient_id).next()
                    patient_cache.put(patient_id, patient_vertex)
                }

                GraphModel.EX.SELF_REPORTED.instance().from(patient_vertex).to(surveyV).create()

            }

            patient_cache = null
            encounter_cache = null

        }

    }

}
