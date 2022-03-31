package example.carnival.micronaut.method



import groovy.transform.ToString
import groovy.util.logging.Slf4j
import javax.inject.Singleton
import javax.inject.Inject
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.Month


import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Direction
import carnival.core.graph.GraphMethods
import carnival.core.graph.GraphMethod
import carnival.core.graph.Core

import example.carnival.micronaut.GraphModel
import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.graph.CarnivalGraph



@ToString(includeNames=true)
@Slf4j 
@Singleton
class Reasoners implements GraphMethods { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    final String DONE = 'Done'
    final String CANCEL = 'Cancel'

    @Inject AppConfig config
    @Inject CarnivalGraph carnivalGraph



    ///////////////////////////////////////////////////////////////////////////
    // STATIC METHODS
    ///////////////////////////////////////////////////////////////////////////





    ///////////////////////////////////////////////////////////////////////////
    // GRAPH METHODS
    ///////////////////////////////////////////////////////////////////////////

    /** */
    class LinkConditionsAndPatients extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                .isa(GraphModel.VX.CONDITION)
            .each { m ->
                log.trace "m: ${m}"
                def patientId = m.PATIENT
                log.trace "patientId: $patientId"

                g.V()
                    .isa(GraphModel.VX.PATIENT)
                    .has(GraphModel.PX.ID, patientId)
                
                .tryNext().ifPresent { pV ->
                    log.trace "pV: $pV"
                    GraphModel.EX.HAS.instance().from(m).to(pV).ensure(g)
                }
            }
        }

    }

    /** NOT the best way, just a demostration of a reasoner method*/
    class LinkCareplansAndPatients extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                .isa(GraphModel.VX.CAREPLAN)//.as('c')
                //.select('c')
            .each { m ->
                log.trace "m: ${m}"
                def patientId = GraphModel.PX.PATIENT.valueOf(m)
                log.trace "patientId: $patientId"

                g.V()
                    .isa(GraphModel.VX.PATIENT)
                    .has(GraphModel.PX.ID, patientId)
                
                .tryNext().ifPresent { pV ->
                    log.trace "pV: $pV"
                    GraphModel.EX.HAS.instance().from(pV).to(m).ensure(g)
                }
            }
        }

    }

}