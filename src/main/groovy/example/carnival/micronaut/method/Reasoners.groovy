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

import example.carnival.micronaut.GraphModel
import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.graph.BiomedCarnival



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
    @Inject BiomedCarnival carnivalGraph



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

            // start with all patients
            def patientVs = g.V().isa(GraphModel.VX.PATIENT).toList()

            // filter by age
            patientVs = g.V(patientVs)
                .has(GraphModel.PX_PATIENT.AGE, P.between(ageMinimumInclusive, ageMaximumInclusive))
            .toList()
                
            // filter for patients with at least two encounters where they were diagnosed
            // with full-time employment
            patientVs = g.V(patientVs).as('patient')
                .out(GraphModel.EX.HAS)
                .isa(GraphModel.VX.ENCOUNTER).as('encounter')
                .out(GraphModel.EX.DIAGNOSED_AT)
                .isa(GraphModel.VX.CONDITION)
                .has(GraphModel.PX.DESCRIPTION, symptomLabel)
                .select('patient', 'encounter')
            .toList()
            .groupBy({ it.patient })
            .findAll({ patientV, recs ->
                recs.size() >= 2
            })
            .collect({ patientV, recs ->
                patientV
            })

            // filter for one diagnosis of hypertension
            patientVs = g.V(patientVs).as('patient')
                .out(GraphModel.EX.HAS)
                .isa(GraphModel.VX.ENCOUNTER)
                .out(GraphModel.EX.DIAGNOSED_AT)
                .isa(GraphModel.VX.CONDITION)
                .has(GraphModel.PX.DESCRIPTION, diagnosisLabel)
                .select('patient')
            .toList()

            // filter for self report
            patientVs = g.V(patientVs).as('patient')
                .out(GraphModel.EX.SELF_REPORTED)
                .isa(GraphModel.VX.SURVEY)
                .has(GraphModel.PX_SURVEY.RESPONSE_TEXT, responseLabel)
                .select('patient')
            .toList()

            // filter for medication
            patientVs = g.V(patientVs).as('patient')
                .out(GraphModel.EX.PRESCRIBED)
                .isa(GraphModel.VX.MEDICATION)
                .has(GraphModel.PX.DESCRIPTION, medicationLabel)
                .select('patient')
            .toList()

            // assign to cohort
            patientVs.each { patV ->
                log.trace "patV: ${patV}"
                GraphModel.EX.HAS.instance().from(ansV).to(patV).ensure(g)
            }

            /*
            g.V()
                .match(
                    __.as("patient").has(GraphModel.PX_PATIENT.AGE, P.between(ageMinimumInclusive, ageMaximumInclusive)),

                    __.as("patient").out(GraphModel.EX.HAS).as("symptomEncounter1"),
                    __.as("symptomEncounter1").out(GraphModel.EX.DIAGNOSED_AT).as("symptomCondition1"),
                    __.as("symptomCondition1").has(GraphModel.PX.DESCRIPTION, symptomLabel).as("c1"),

                    __.as("patient").out(GraphModel.EX.HAS).as("symptomEncounter2"),
                    __.as("symptomEncounter2").out(GraphModel.EX.DIAGNOSED_AT).as("symptomCondition2"),
                    __.as("symptomCondition2").has(GraphModel.PX.DESCRIPTION, symptomLabel).as("c2"),

                    __.as("symptomEncounter1").where(P.neq("symptomEncounter2")),

                    __.as("patient").out(GraphModel.EX.HAS).as("diagnosisEncounter"),
                    __.as("diagnosisEncounter").out(GraphModel.EX.DIAGNOSED_AT).as("diagnosisCondition"),
                    __.as("diagnosisCondition").has(GraphModel.PX.DESCRIPTION, diagnosisLabel).as("c3"),

                    __.as("patient").out(GraphModel.EX.SELF_REPORTED).as("survey"),
                    __.as("survey").has(GraphModel.PX_SURVEY.RESPONSE_TEXT, responseLabel),

                    __.as("patient").out(GraphModel.EX.PRESCRIBED).as("medication"),
                    __.as("medication").has(GraphModel.PX.DESCRIPTION, medicationLabel)

                )

                .select("patient")
                .dedup()

            .each { patV ->
                log.trace "patV: ${patV}"
                GraphModel.EX.HAS.instance().from(ansV).to(patV).ensure(g)
            }
            */
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

            def cohortPatients = g.V()
                .isa(GraphModel.VX.COHORT_PATIENTS)
                .out(GraphModel.EX.HAS)
                .isa(GraphModel.VX.PATIENT)
            .toList()

            def controlPatients = g.V()
                .isa(GraphModel.VX.PATIENT).as('p')

                .has(GraphModel.PX_PATIENT.AGE, P.between(ageMinimumInclusive, ageMaximumInclusive))

                .out(GraphModel.EX.SELF_REPORTED)
                .isa(GraphModel.VX.SURVEY)
                .has(GraphModel.PX_SURVEY.RESPONSE_TEXT, responseLabel)

                .select('p')
                .out(GraphModel.EX.PRESCRIBED)
                .isa(GraphModel.VX.MEDICATION)
                .has(GraphModel.PX.DESCRIPTION, medicationLabel)

                .select('p')
            .dedup()
            .toList()

            /*def controlPatients =
                g.V().match(
                    __.as("patient").has(GraphModel.PX_PATIENT.AGE, P.between(ageMinimumInclusive, ageMaximumInclusive)),

                    __.as("patient").out(GraphModel.EX.SELF_REPORTED).as("survey"),
                    __.as("survey").has(GraphModel.PX_SURVEY.RESPONSE_TEXT, responseLabel),

                    __.as("patient").out(GraphModel.EX.PRESCRIBED).as("medication"),
                    __.as("medication").has(GraphModel.PX.DESCRIPTION, medicationLabel)
                )
                .select("patient")
                .dedup()
                .toList()*/

            controlPatients.removeAll(cohortPatients)

            def controlV = GraphModel.VX.CONTROL_PATIENTS.instance().ensure(graph, g)

            controlPatients.each { p -> GraphModel.EX.HAS.instance().from(controlV).to(p).ensure(g)}
        }
    }
}