package example.carnival.micronaut.method

import carnival.graph.ControlledInstance
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex
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

//    static Map<String, Neo4jVertex> patient_cache = new HashMap<String, Neo4jVertex>()
    static Map<String, ControlledInstance> patient_cache = new HashMap<String, ControlledInstance>()

    static Map<String, Neo4jVertex> encounter_cache = new HashMap<String, Neo4jVertex>()

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

//                log.trace "rec: ${rec}"
                def patient_vertex = GraphModel.VX.PATIENT.instance().withProperties(
                    GraphModel.PX.ID, rec.ID,
                    GraphModel.PX_PATIENT.BIRTH_DATE, rec.BIRTH_DATE,
//                    GraphModel.PX_PATIENT.DEATH_DATE, rec.DEATH_DATE, //death_date always null
                    GraphModel.PX_PATIENT.FIRST_NAME, rec.FIRST_NAME,
                    GraphModel.PX_PATIENT.LAST_NAME, rec.LAST_NAME,
                    GraphModel.PX_PATIENT.LATITUDE, rec.LATITUDE,
                    GraphModel.PX_PATIENT.LONGITUDE, rec.LONGITUDE,
                    GraphModel.PX_PATIENT.AGE, age
                )
                def foo = patient_vertex.create(graph)

                patient_cache.put(patient_id, foo)

////                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//
////                def age = Period.between(LocalDateTime.parse(rec.BIRTH_DATE, formatter), LocalDateTime.now()).getYears()
//                def age = Period.between(LocalDateTime.parse(rec.BIRTH_DATE, "yyyy-MM-dd HH:mm:ss"), LocalDateTime.now()).getYears()

//                patV.ensure(graph, g)

//                patV.create(graph)
            }

        }

    }

    /** */
    class LoadEncounters extends GraphMethod {

        void execute(Graph graph, GraphTraversalSource g) {

            log.info "hi :)"

            def mdt = exampleDbVine
                .method('Encounters')
                .call()
            .result

//            Map<String, Neo4jVertex> patient_cache = new HashMap<String, Neo4jVertex>()

            mdt.data.values().each { rec ->
                log.trace "rec: ${rec}"
//                def encV = GraphModel.VX.ENCOUNTER.instance().withProperties(
//                    GraphModel.PX.ID, rec.ENCOUNTER_ID,
//                    GraphModel.PX.START, rec.START,
//                    GraphModel.PX.END, rec.STOP
//                )
//                def foo = encV.create(graph)
                def encV = GraphModel.VX.ENCOUNTER.instance().withProperties(
                        GraphModel.PX.ID, rec.ENCOUNTER_ID,
                        GraphModel.PX.START, rec.START,
                        GraphModel.PX.END, rec.STOP
                ).create(graph)

                encounter_cache.put(rec.ENCOUNTER_ID, encV)

//                log.info "enc vertex class: ${encV.getClass().toString()}"

                String patient_id = rec.PATIENT_ID

                def patient_vertex

                if (patient_cache.containsKey(patient_id)) {
                    patient_vertex = patient_cache.get(patient_id)
                }

                else {
                    patient_vertex = g.V().isa(GraphModel.VX.PATIENT).has(GraphModel.PX.ID, patient_id).next()
                    patient_cache.put(patient_id, patient_vertex)
                }

//                GraphModel.EX.HAS.instance().from(patient_vertex).to(encV).create()
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

//            Map<String, Neo4jVertex> patient_cache = new HashMap<String, Neo4jVertex>()
//            Map<String, Neo4jVertex> encounter_cache = new HashMap<String, Neo4jVertex>()

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

                Neo4jVertex patient_vertex
                Neo4jVertex encounter_vertex

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

//                g.V()
//                    .isa(GraphModel.VX.PATIENT)
//                    .has(GraphModel.PX.ID, patient_id)
//                .each { patV ->
//                    log.trace "patV: ${patV} Patient: ${patient_id}"
//                    GraphModel.EX.DIAGNOSED_WITH.instance().from(patV).to(conditionV).create()
//                }
//                g.V()
//                    .isa(GraphModel.VX.ENCOUNTER)
//                    .has(GraphModel.PX.ID, encounter_id)
//                .each { encV ->
//                    log.trace "encV: ${encV} Encounter: ${encounter_id}"
//                    GraphModel.EX.DIAGNOSED_AT.instance().from(encV).to(conditionV).create()
//                }
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

//            Map<String, Neo4jVertex> patient_cache = new HashMap<String, Neo4jVertex>()
//            Map<String, Neo4jVertex> encounter_cache = new HashMap<String, Neo4jVertex>()

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

//            Map<String, Neo4jVertex> cache = new HashMap<String, Neo4jVertex>()

            //dt.eachWithIndex { rec, index ->
//            int count = 0
//            for (rec in dt) {

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

        }

    }


    class PrintGraph14 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {

            g.V()
                    .match(
                            __.as("patient").has(GraphModel.PX_PATIENT.AGE, P.between(18, 35)),
//                        __.as("patient").out(GraphModel.EX.HAS).as("encounter").count().is(P.gte(2)),
                            __.as("patient").out(GraphModel.EX.HAS).as("symptomEncounter1"),
                            __.as("symptomEncounter1").out(GraphModel.EX.DIAGNOSED_AT).as("symptomCondition1"),
                            __.as("symptomCondition1").has(GraphModel.PX.DESCRIPTION, "Full-time employment (finding)").as("c1"),

                            __.as("patient").out(GraphModel.EX.HAS).as("symptomEncounter2"),
                            __.as("symptomEncounter2").out(GraphModel.EX.DIAGNOSED_AT).as("symptomCondition2"),
                            __.as("symptomCondition2").has(GraphModel.PX.DESCRIPTION, "Full-time employment (finding)").as("c2"),

//                            __.as("symptomEncounter1").where(P.neq("symptomEncounter2")),

//                            __.as("patient").out(GraphModel.EX.HAS).as("symptomEncounter1"),
//                            __.as("symptomEncounter1").out(GraphModel.EX.DIAGNOSED_AT).as("diagnosis"),
//                            __.as("diagnosisCondition").has(GraphModel.PX.DESCRIPTION, "Hypertension").as("c3"),

                            __.as("patient").out(GraphModel.EX.HAS).as("diagnosisEncounter"),
                            __.as("diagnosisEncounter").out(GraphModel.EX.DIAGNOSED_AT).as("diagnosisCondition"),
                            __.as("diagnosisCondition").has(GraphModel.PX.DESCRIPTION, "Hypertension").as("c3")

//                        __.as("patient").out(GraphModel.EX.HAS).as("encounter2"),
//                        __.as("encounter1").
//                        __.as("encounter").in(GraphModel.EX.HAS).as("patient"),
//                        __.as("encounter").out(GraphModel.EX.DIAGNOSED_AT).as("condition")
//                        __.as("patient").out(GraphModel.EX.DIAGNOSED_WITH).as("condition"),
//                            __.as("condition").has(GraphModel.PX.DESCRIPTION, "Hypertension").as("c").count().is(P.gte(2)),
//                        __.as("condition").has(GraphModel.PX.DESCRIPTION, "Hypertension").count().is(P.gte(2)),

//
//                            __.as("patient").out(GraphModel.EX.SELF_REPORTED).as("survey"),
//                            __.as("survey").has(GraphModel.PX_SURVEY.RESPONSE_TEXT, "Never smoker"),
//                            __.as("patient").out(GraphModel.EX.PRESCRIBED).as("medication"),
//                            __.as("medication").has(GraphModel.PX.DESCRIPTION, "lisinopril 10 MG Oral Tablet")
//                        lisinopril 10 MG
//                        __.as("encounter").out(GraphModel.EX.PRESCRIBED_AT).as("medication")
                    )
//                .select("patient", "encounter").dedup()
//                .select("patient", "medication")
//                .select("medication")
//                    .select("patient")
//                    .select("c1", "c2")
//                    .select("patient", "symptomEncounter1", "symptomEncounter2", "diagnosisEncounter")
                    .select("symptomEncounter1")
                    .dedup()
//                    .count()
//                .valueMap()

                    .each { v ->
                        log.info "${v}"
//                        log.info "${v.p}"
                        v.properties().each { p ->
                            log.info "v: ${v}, p: ${p.label()} ${p}"
                        }
                    }
        }
    }


//                    .isa(GraphModel.VX.PATIENT)
////                .has(GraphModel.PX.ID, P.eq("7bbb9d0b-bdbd-c4b3-7a7f-422de8cd695f"))
////                    .has(GraphModel.PX_PATIENT.FIRST_NAME, P.eq("Arlen68"))
//                .as('p')
////                    .has(GraphModel.PX_PATIENT.BIRTH_DATE, P.eq("1992-11-12 00:00:00"))
//                .out(GraphModel.EX.HAS)
//                .isa(GraphModel.VX.ENCOUNTER).as('e')
////                    .out(GraphModel.EX.DIAGNOSED_AT)
////                    .isa(GraphModel.VX.CONDITION).as('c')
////                    .has(GraphModel.PX.DESCRIPTION, P.eq("Hypertension"))
////                .select('p', 'e')
////                .select('e')
////                .count()
////                .instances()
//                .select('p')

//    g.V().hasLabel("software")
//.in("created")
//.groupCount().by("name").as("cnt")
//.select("cnt")

    class PrintGraph12 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
//                    .isa(GraphModel.VX.CONDITION).as("c")
//                    .has(GraphModel.PX.DESCRIPTION, P.eq("Hypertension"))
//                    .in(GraphModel.EX.DIAGNOSED_AT).as("e")
//                .
//                .valueMap()
            .has("code", "423315002")

//                    .isa(GraphModel.VX.ENCOUNTER)
//
//                    .in(GraphModel.EX.HAS).as("p") //patient
//
//                    .groupCount().by("id").as("grouped")
//                    .has(GraphModel.PX.ID, P.eq("7bbb9d0b-bdbd-c4b3-7a7f-422de8cd695f"))

                    .each { v ->
                        log.info "${v}"
                    }

        }
    }

//    12:29:20.361 [main] INFO  e.c.micronaut.method.ExampleMethods - [id:[dd95919a-b792-de66-38a2-e1567d2dd5b9], start:[2005-08-28 22:12:36], end:[2005-08-29 22:12:36], nameSpace:[example.carnival.micronaut.GraphModel$VX]]
//12:29:20.361 [main] INFO  e.c.micronaut.method.ExampleMethods - [id:[d9954447-d1f1-1b26-84b9-8f6d4681396c], start:[2013-05-12 22:12:36], end:[2013-05-13 22:12:36], nameSpace:[example.carnival.micronaut.GraphModel$VX]]
//12:

//    g.V().hasLabel("Country")
//    .project("country","airport")
//    .by(valueMap(true))
//    .by(out('hasAirport').valueMap(true).fold())

    class PrintGraph11 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                    .isa(GraphModel.VX.PATIENT).as('p')
                    .group().by(TinkerpopTraversalExtension.out(GraphModel.EX.HAS).count()).by("id")

                    .limit(10)
                    .each { v ->
                        log.info "${v}"
                    }

        }
    }

//    class PrintGraph9 extends GraphMethod {
//        void execute(Graph graph, GraphTraversalSource g) {
//            g.V()
//                    .isa(GraphModel.VX.PATIENT).as("pat")
//                    .match(
//                            __.as("a").out(GraphModel.EX.HAS).count().is(gt(1)
//                            )
//                                    .select("a")
//
//                                    .each { v ->
//                                        log.info "${v}"
////                        log.info "${v.p}"
////                        v.properties().each { p ->
////                            log.info "v: ${v}, p: ${p.label()} ${p}"
////                        }
//                                    }
//        }
//    }

//    g.V()
//    .isa(GraphModel.VX.PATIENT).as("p")
//    .out(GraphModel.VX.ENCOUNTER).as("e")
//    .select("p", "e")
//
//
//    g.V().group().by( __.out().count() )

    class PrintGraph9 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                    .isa(GraphModel.VX.PATIENT).as("pat")
                    .match(
                            __.as("a").out(GraphModel.EX.HAS).count().is(P.gt(1))
                    )
                    .select("a")
                    .valueMap()

                    .each { v ->
                        log.info "${v}"
//                        log.info "${v.p}"
//                        v.properties().each { p ->
//                            log.info "v: ${v}, p: ${p.label()} ${p}"
//                        }
                    }
        }
    }

    class PrintGraph8 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                .isa(GraphModel.VX.PATIENT).as("pat")

                .out(GraphModel.EX.HAS)
                .group().by("id").as("grouped")                       // works
//                .group().by(GraphModel.PX.ID).as("grouped")         // doesnt work
//                .group().by(GraphModel.VX.ENCOUNTER).as("grouped")  // also doesnt work
                .select("grouped")

                .each { v ->
                    log.info "${v}"
//                        log.info "${v.p}"
//                        v.properties().each { p ->
//                            log.info "v: ${v}, p: ${p.label()} ${p}"
//                        }
                }

        }
    }

    //  GraphMethod.result
//  Map result = [:]

    class PrintGraph7 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
//             .match(
//                     __.as("a").isa(GraphModel.VX.PATIENT)
//             )
                .isa(GraphModel.VX.PATIENT)
//                .has(GraphModel.PX.ID, P.eq("7bbb9d0b-bdbd-c4b3-7a7f-422de8cd695f"))
//                    .has(GraphModel.PX_PATIENT.FIRST_NAME, P.eq("Arlen68"))
                .as('p')
//                    .has(GraphModel.PX_PATIENT.BIRTH_DATE, P.eq("1992-11-12 00:00:00"))
                .out(GraphModel.EX.HAS)
                .isa(GraphModel.VX.ENCOUNTER).as('e')
//                    .out(GraphModel.EX.DIAGNOSED_AT)
//                    .isa(GraphModel.VX.CONDITION).as('c')
//                    .has(GraphModel.PX.DESCRIPTION, P.eq("Hypertension"))
//                .select('p', 'e')
//                .select('e')
//                .count()
//                .instances()
                .select('p')


//                .group().by(GraphModel.VX.ENCOUNTER)


                .each { v ->
                    log.info "${v}"
//                        log.info "${v.p}"
//                        v.properties().each { p ->
//                            log.info "v: ${v}, p: ${p.label()} ${p}"
//                        }
                    }

        }
    }



    class PrintGraph6 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                    .isa(GraphModel.VX.PATIENT)


                    .has(GraphModel.PX.ID, P.eq("7bbb9d0b-bdbd-c4b3-7a7f-422de8cd695f"))
//                    .has(GraphModel.PX_PATIENT.FIRST_NAME, P.eq("Arlen68"))
                    .as('p')
//                    .has(GraphModel.PX_PATIENT.BIRTH_DATE, P.eq("1992-11-12 00:00:00"))
                    .out(GraphModel.EX.HAS)
                    .isa(GraphModel.VX.ENCOUNTER).as('e')
//                    .out(GraphModel.EX.DIAGNOSED_AT)
//                    .isa(GraphModel.VX.CONDITION).as('c')
//                    .has(GraphModel.PX.DESCRIPTION, P.eq("Hypertension"))
                    .select('p', 'e')
//                    .select('p')
                    .group().by('e')
//                    .count()


                    .each { v ->
                        log.info "${v}"
//                        log.info "${v.p}"
//                        v.properties().each { p ->
//                            log.info "v: ${v}, p: ${p.label()} ${p}"
//                        }

                    }

        }
    }



    class PrintGraph5 extends GraphMethod {
//        g.V()
//        .isa(GraphModel.VX.PATIENT).as('p')
//        .has(GraphModel.PX_PATIENT.AGE, org.apache.tinkerpop.gremlin.process.traversal.P.between(18, 55))
//        .out(GraphModel.EX.HAS)
//        .isa(GraphModel.VX.ENCOUNTER).as('e')
//        .select('p', 'e')
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                    .isa(GraphModel.VX.PATIENT).as('p')

                    .out(GraphModel.EX.HAS)
                    .isa(GraphModel.VX.ENCOUNTER).as('e')

                    .select('p', 'e')
                    .groupCount().by('p')

//                    .isa(GraphModel.VX.ENCOUNTER).as('e')
//                    .out(GraphModel.EX.DIAGNOSED_AT)
//                    .isa(GraphModel.VX.CONDITION).as('c')
//                    .has(GraphModel.PX.DESCRIPTION, P.eq("Hypertension"))
//                    .select('p', 'e').groupCount().by

                    .each { v ->
                        log.info "${v}"
                    }

        }
    }

    class PrintGraph4 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                    .isa(GraphModel.VX.PATIENT)
//                    .union(
//                            .out(GraphModel.EX.HAS)
//                            .out(GraphModel.EX.DIAGNOSED_WITH)
//                    ).as('foo')
//                    .select('foo')
//                    .filter(both()
//
//                            out(GraphModel.EX.HAS).count().is(gte(2)))
                    .each { v ->
                        log.info "${v}"

                        v.properties().each { p ->
                            log.info "v: ${v}, p: ${p.label()} ${p}"
                        }
                    }

        }
    }

    class PrintGraph3 extends GraphMethod {
        void execute(Graph graph, GraphTraversalSource g) {
            def count = g.V()
                    .isa(GraphModel.VX.PATIENT).as('p')
                    .has(GraphModel.PX_PATIENT.AGE, 33)
//                    .out(GraphModel.EX.HAS)
//                    .union(
                            .out(GraphModel.EX.HAS)
                            .isa(GraphModel.VX.ENCOUNTER).as('e1')
                            .out(GraphModel.EX.DIAGNOSED_AT)
                            .isa(GraphModel.VX.CONDITION).as('c1')
//            ,
//                            out(GraphModel.EX.HAS)
//                            .isa(GraphModel.VX.ENCOUNTER).as('e2')
//                            .out(GraphModel.EX.DIAGNOSED_AT)
//                            .isa(GraphModel.VX.CONDITION).as('c2')
//                    )
                    .select('p', 'e1', 'c1')
//                    .select('p', 'e1', 'c1', 'e2', 'c2')
//                    .count().result

//            log.info "PrintGraph3() count = ${count}"

        }
    }

    class PrintGraph2 extends GraphMethod {
//        g.V()
//        .isa(GraphModel.VX.PATIENT).as('p')
//        .has(GraphModel.PX_PATIENT.AGE, org.apache.tinkerpop.gremlin.process.traversal.P.between(18, 55))
//        .out(GraphModel.EX.HAS)
//        .isa(GraphModel.VX.ENCOUNTER).as('e')
//        .select('p', 'e')
        void execute(Graph graph, GraphTraversalSource g) {
            g.V()
                    .isa(GraphModel.VX.PATIENT).as('p')
                    .has(GraphModel.PX_PATIENT.AGE, P.eq(33))
//                    .has(GraphModel.PX_PATIENT.BIRTH_DATE, P.eq("1992-11-12 00:00:00"))
                    .out(GraphModel.EX.HAS)
                    .isa(GraphModel.VX.ENCOUNTER).as('e')
                    .out(GraphModel.EX.DIAGNOSED_AT)
                    .isa(GraphModel.VX.CONDITION).as('c')
                    .has(GraphModel.PX.DESCRIPTION, P.eq("Hypertension"))
                    .select('p', 'e', 'c')

                    .each { v ->
                        log.info "${v} ${v.p} ${v.e} ${v.c}"
//                        log.info "${v} ${v.label()}"
////            g.V().each { v ->
//                        log.info "v: ${v} ${v.label()}"
//
                        v.p.properties().each { p ->
                            log.info "v: ${v}, p: ${p.label()} ${p}"
                        }
//
//                        g.V(v).outE(GraphModel.EX.HAS).each { e ->
//                            log.info "v: ${v}, e: ${e} ${e.label()}"
//
//                            e.properties().each { p ->
////                        log.trace "e: ${e} p: ${p.label()} ${p}" //TH5: p.label() doesn't work
//                                log.info "e: ${e} p: ${p}"
//                            }
//                        }
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