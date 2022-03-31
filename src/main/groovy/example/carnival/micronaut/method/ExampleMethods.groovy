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
                def patV = GraphModel.VX.PATIENT.instance().withProperties(
                    GraphModel.PX.ID, rec.PATIENT_ID,
                    GraphModel.PX_PATIENT.BIRTH_DATE, rec.BIRTHDATE,
                    // GraphModel.PX_PATIENT.DEATH_DATE, rec.DEATHDATE,
                    GraphModel.PX_PATIENT.FIRST_NAME, rec.FIRST,
                    GraphModel.PX_PATIENT.LAST_NAME, rec.LAST,
                    GraphModel.PX_PATIENT.LATITUDE, rec.LAT,
                    GraphModel.PX_PATIENT.LONGITUDE, rec.LON
                ).ensure(graph, g)
                //TH5: what is the difference between graph and g?
//                GraphModel.EX.HAS.instance().from(patV).to(encV).create(graph, g)
                GraphModel.EX.HAS.instance().from(patV).to(encV).ensure(g)
            }
        }
    }

    /** */
    class LoadConditions extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def mdt = exampleDbVine
                .method('Conditions')
                .call()
            .result

            mdt.data.values().each { rec ->
                log.trace "rec: ${rec}"
                def encV = GraphModel.VX.CONDITION.instance().withProperties(
                    GraphModel.PX.START, rec.START,
                    GraphModel.PX.END, rec.STOP,
                    GraphModel.PX.PATIENT, rec.PATIENT_ID,
                    GraphModel.PX.CODE, rec.CODE,
                    GraphModel.PX.DESCRIPTION, rec.DESCRIPTION
                ).ensure(graph, g)
            }
        }
    }

    /** */
    class LoadCareplans extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def mdt = exampleDbVine
                .method('Careplans')
                .call()
            .result

            mdt.data.values().each { rec ->
                log.trace "rec: ${rec}"
                def encV = GraphModel.VX.CAREPLAN.instance().withProperties(
                    GraphModel.PX.ID, rec.CAREPLAN_ID,
                    GraphModel.PX.START, rec.START,
                    //GraphModel.PX.END, rec.STOP,
                    GraphModel.PX.PATIENT, rec.PATIENT_ID,
                    GraphModel.PX.ENCOUNTER, rec.ENCOUNTER_ID,
                    GraphModel.PX.CODE, rec.CODE,
                    GraphModel.PX.DESCRIPTION, rec.DESCRIPTION
                ).ensure(graph, g)
            }
        }
    }






}