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
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.process.traversal.P
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

    class FindResearchAnswer extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {

            def ansV = GraphModel.VX.RESEARCH_ANSWER.instance().ensure(graph, g)

            g.V()
                .isa(GraphModel.VX.PATIENT).as('p')
                .has(GraphModel.PX_PATIENT.AGE, P.between(18, 55))
                .out(GraphModel.EX.HAS)
                .isa(GraphModel.VX.ENCOUNTER).as('e')

                .select('p', 'e')
                .toList()
                .groupBy({it.p})
                .collect({it.key})
                .each { m ->
                    log.trace "m: ${m}"
                    GraphModel.EX.CONTAINS.instance().from(ansV).to(m).ensure(g)
                }
        }    
    }

}