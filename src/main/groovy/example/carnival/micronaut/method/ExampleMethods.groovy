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
//import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.vine.ExampleDbVine
//import example.carnival.micronaut.graph.CarnivalGraph



@ToString(includeNames=true)
@Slf4j 
@Singleton
class ExampleMethods implements GraphMethods { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

//    @Inject AppConfig config
    @Inject ExampleDbVine exampleDbVine
//    @Inject CarnivalGraph carnivalGraph




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
                    GraphModel.PX.ID, rec.PATIENT_ID
//                    ,
//                    GraphModel.PX.START, rec.START,
//                    GraphModel.PX.END, rec.STOP
                ).ensure(graph, g)
                //TH5: what is the difference between graph and g?
//                GraphModel.EX.HAS.instance().from(patV).to(encV).create(graph, g)
                GraphModel.EX.HAS.instance().from(patV).to(encV).ensure(g)
            }

        }

    }


    class PrintEncounters extends GraphMethod {
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