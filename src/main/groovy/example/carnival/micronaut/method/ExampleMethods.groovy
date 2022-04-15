package example.carnival.micronaut.method



import groovy.transform.ToString
import groovy.util.logging.Slf4j
import javax.inject.Singleton
import javax.inject.Inject

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import carnival.core.graph.GraphMethods
import carnival.core.graph.GraphMethod

import example.carnival.micronaut.GraphModel
import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.vine.ExampleDbVine
import example.carnival.micronaut.graph.CarnivalGraph
import carnival.util.DataTable



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




    ///////////////////////////////////////////////////////////////////////////
    // SERVICE METHOD
    ///////////////////////////////////////////////////////////////////////////
    
    class LoadPatients extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def mdt = exampleDbVine
                .method('Patients')
                .call()
                .result

            mdt.data.values().each { rec ->
                log.trace "rec: ${rec}"
                def patV = GraphModel.VX.PATIENT.instance()
                //.withMatchingProperties(rec
                .withProperties(
                    GraphModel.PX.ID, rec.ID,
                    GraphModel.PX_PATIENT.BIRTH_DATE, rec.BIRTH_DATE,
//                    GraphModel.PX_PATIENT.DEATH_DATE, rec.DEATH_DATE, //death_date always null
                    GraphModel.PX_PATIENT.FIRST_NAME, rec.FIRST_NAME,
                    GraphModel.PX_PATIENT.LAST_NAME, rec.LAST_NAME
                    //GraphModel.PX_PATIENT.LATITUDE, rec.LATITUDE,
                    //GraphModel.PX_PATIENT.LONGITUDE, rec.LONGITUDE
                ).ensure(graph, g)

//                ).create(graph)
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
                ).ensure(graph, g)

                def patient_id = rec.PATIENT_ID

                g.V()
                    .isa(GraphModel.VX.PATIENT)
                    .has(GraphModel.PX.ID, patient_id)
                .each { patV ->
                    GraphModel.EX.HAS.instance().from(patV).to(encV).create()
                }
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
                def conditionVBuilder = GraphModel.VX.CONDITION.instance()
                    .withProperty(GraphModel.PX.START, rec.START)
                    .withNonNullProperties(GraphModel.PX.END, rec.END)
                    .withProperty(GraphModel.PX.CODE, rec.CODE)
                    .withProperty(GraphModel.PX.DESCRIPTION, rec.DESCRIPTION)

                def conditionV = conditionVBuilder.create(graph)

                def patient_id = rec.PATIENT_ID
                def encounter_id = rec.ENCOUNTER_ID

                g.V()
                    .isa(GraphModel.VX.PATIENT)
                    .has(GraphModel.PX.ID, patient_id)
                .each { patV ->
                    log.trace "patV: ${patV} Patient: ${patient_id}"
                    GraphModel.EX.DIAGNOSED_WITH.instance().from(patV).to(conditionV).create()
                }
                g.V()
                    .isa(GraphModel.VX.ENCOUNTER)
                    .has(GraphModel.PX.ID, encounter_id)
                .each { encV ->
                    log.trace "encV: ${encV} Encounter: ${encounter_id}"
                    GraphModel.EX.DIAGNOSED_AT.instance().from(encV).to(conditionV).create()
                }
            }
        }
    }
    
    /** */
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
                .ensure(graph, g)

                def patient_id = rec.PATIENT_ID
                def encounter_id = rec.ENCOUNTER_ID

                g.V()
                    .isa(GraphModel.VX.PATIENT)
                    .has(GraphModel.PX.ID, patient_id)
                .each { patV ->
                    log.trace "patV: ${patV} Patient: ${patient_id}"
                    GraphModel.EX.PRESCRIBED.instance().from(patV).to(medV).create()
                }
                g.V()
                    .isa(GraphModel.VX.ENCOUNTER)
                    .has(GraphModel.PX.ID, encounter_id)
                .each { encV ->
                    log.trace "encV: ${encV} Encounter: ${encounter_id}"
                    GraphModel.EX.PRESCRIBED_AT.instance().from(encV).to(medV).create()
                }
            }
        }
    }

    class LoadSurveys extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {
            
            def dt = DataTable.readDataFromCsvFile('data\\survey\\observations_survey.csv')

            //dt.eachWithIndex { rec, index ->
            int count = 0
            for (rec in dt) {
                
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
                
                g.V()
                    .isa(GraphModel.VX.PATIENT)
                    .has(GraphModel.PX.ID, patient_id)
                .each { patV ->
                    log.trace "patV: ${patV} Patient: ${patient_id}"
                    GraphModel.EX.SELF_REPORTED.instance().from(patV).to(surveyV).create()
                }
                count++
                if (count > 100) break
            }

        }

    }

    class PrintGraph extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            // g.V().isa(GraphModel.VX.ENCOUNTER).each {
            g.V().each { v ->
                log.trace "v: ${v} ${v.label()}"

                v.properties().each { p ->
                    log.trace "v: ${v}, p: ${p.label()} ${p}"
                }

                g.V(v).outE(GraphModel.EX.HAS).each { e ->
                    log.trace "v: ${v}, e: ${e} ${e.label()}"

                    e.properties().each { p ->
//                        log.trace "e: ${e} p: ${p.label()} ${p}" //TH5: p.label() doesn't work
                        log.trace "e: ${e} p: ${p}"
                    }
                }
            }

        }
    }






}