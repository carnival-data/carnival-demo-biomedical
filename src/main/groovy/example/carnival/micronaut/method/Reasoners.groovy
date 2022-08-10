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
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
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

            def symptomLabel = "Full-time employment (finding)"
            def symptomCode = "160903007"

            def diagnosisLabel = "Hypertension"
            def diagnosisCode = "59621000"

            def ageMinimumInclusive = 18
            def ageMaximumInclusive = 35

            def responseLabel = "Former smoker"

            def medicationLabel = "lisinopril 10 MG Oral Tablet"

            def ansV = GraphModel.VX.COHORT_PATIENTS.instance().ensure(graph, g)

            g.V()
                    .match(
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("patient").has(GraphModel.PX_PATIENT.AGE, P.between(ageMinimumInclusive, ageMaximumInclusive)),

//                        __.as("patient").out(GraphModel.EX.HAS).as("encounter").count().is(P.gte(2)),

                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("patient").out(GraphModel.EX.HAS).as("symptomEncounter1"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("symptomEncounter1").out(GraphModel.EX.DIAGNOSED_AT).as("symptomCondition1"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("symptomCondition1").has(GraphModel.PX.DESCRIPTION, symptomLabel).as("c1"),

                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("patient").out(GraphModel.EX.HAS).as("symptomEncounter2"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("symptomEncounter2").out(GraphModel.EX.DIAGNOSED_AT).as("symptomCondition2"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("symptomCondition2").has(GraphModel.PX.DESCRIPTION, symptomLabel).as("c2"),

                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("symptomEncounter1").where(P.neq("symptomEncounter2")),

                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("patient").out(GraphModel.EX.HAS).as("diagnosisEncounter"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("diagnosisEncounter").out(GraphModel.EX.DIAGNOSED_AT).as("diagnosisCondition"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("diagnosisCondition").has(GraphModel.PX.DESCRIPTION, diagnosisLabel).as("c3"),
//
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("patient").out(GraphModel.EX.SELF_REPORTED).as("survey"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("survey").has(GraphModel.PX_SURVEY.RESPONSE_TEXT, responseLabel),

                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("patient").out(GraphModel.EX.PRESCRIBED).as("medication"),
                            org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as("medication").has(GraphModel.PX.DESCRIPTION, medicationLabel)

                    )

//                    .select("patient", "symptomEncounter1", "symptomEncounter2", "diagnosisEncounter")
//                    .select("patient","diagnosisEncounter","survey")
                    .select("patient")
                    .dedup()
//                    .count()
//                    .valueMap()

                    .each { patV ->
                        log.trace "patV: ${patV}"
                        GraphModel.EX.CONTAINS.instance().from(ansV).to(patV).ensure(g)
                    }
        }
    }

    class FindControlPatients extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            def symptomLabel = "Full-time employment (finding)"
            def symptomCode = "160903007"

            def diagnosisLabel = "Hypertension"
            def diagnosisCode = "59621000"

            def ageMinimumInclusive = 18
            def ageMaximumInclusive = 35

            def responseLabel = "Former smoker"

            def medicationLabel = "lisinopril 10 MG Oral Tablet"

//            def ansV = GraphModel.VX.CONTROL_PATIENTS.instance().ensure(graph, g)

            def cohortPatients = g.V().isa(GraphModel.VX.COHORT_PATIENTS).out(GraphModel.EX.CONTAINS).toList()

            def controlPatients =
                g.V().match(
                    __.as("patient").has(GraphModel.PX_PATIENT.AGE, P.between(ageMinimumInclusive, ageMaximumInclusive)),

                    __.as("patient").out(GraphModel.EX.SELF_REPORTED).as("survey"),
                    __.as("survey").has(GraphModel.PX_SURVEY.RESPONSE_TEXT, responseLabel),

                    __.as("patient").out(GraphModel.EX.PRESCRIBED).as("medication"),
                    __.as("medication").has(GraphModel.PX.DESCRIPTION, medicationLabel)
                )
                .select("patient")
                .dedup()
                .toList()

            System.out.println(controlPatients)
            controlPatients.removeAll(cohortPatients)

            def controlV = GraphModel.VX.CONTROL_PATIENTS.instance().ensure(graph, g)
//            GraphModel.EX.CONTAINS.instance().from(ansV).to(patV).ensure(g)

            System.out.println(controlPatients)

            controlPatients.each { p -> GraphModel.EX.HAS.instance().from(controlV).to(p).ensure(g)}
        }
    }
}