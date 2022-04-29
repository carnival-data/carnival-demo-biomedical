package example.carnival.micronaut.vine



import groovy.transform.ToString
import groovy.util.logging.Slf4j
import javax.inject.Singleton
import javax.inject.Inject
import groovy.sql.Sql
import static java.sql.ResultSet.*
import io.micronaut.context.annotation.Property

import carnival.util.GenericDataTable
import carnival.util.MappedDataTable
import carnival.core.vine.Vine
import carnival.core.vine.MappedDataTableVineMethod
import carnival.core.vine.GenericDataTableVineMethod

import example.carnival.micronaut.config.AppConfig



@ToString(includeNames=true)
@Slf4j 
@Singleton
class ExampleDbVine implements Vine { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject AppConfig config



    ///////////////////////////////////////////////////////////////////////////
    // UTILITY
    ///////////////////////////////////////////////////////////////////////////

    Sql connect() {
        Sql.newInstance(
            driver: "org.postgresql.Driver",
            // jdbc:postgresql://db:5432/EHR
            url: "jdbc:postgresql://${config.exampleDb.server}:${config.exampleDb.port}/${config.exampleDb.databaseName}",
            user: config.exampleDb.user,
            password: config.exampleDb.password
        )
        
    }


    abstract class MappedMethod extends MappedDataTableVineMethod { 
        
        abstract String getQuery()
        abstract Map getDataTableArgs()
        
        MappedDataTable fetch(Map args) {
            log.trace "database connect()"
            def sql = connect()

            def mdt = createDataTable( dataTableArgs )
            log.debug "query: ${query}"

            try {
                log.trace "sql.eachRow()"
                sql.eachRow(query) { row ->
                    log.trace "row: $row"
                    mdt.dataAdd(row)
                }
            } finally {
                if (sql) sql.close()
            }
            mdt
        }

    }

    abstract class GenericMappedMethod extends GenericDataTableVineMethod { 
        
        abstract String getQuery()
        
        GenericDataTable fetch(Map args) {
            log.trace "database connect()"
            def sql = connect()

            def gdt = createDataTable()
            log.debug "query: ${query}"

            try {
                log.trace "sql.eachRow()"
                sql.eachRow(query) { row ->
                    log.trace "row: $row"
                    gdt.dataAdd(row)
                }
            } finally {
                if (sql) sql.close()
            }
            gdt
        }

    }


    ///////////////////////////////////////////////////////////////////////////
    // VINE METHODS
    ///////////////////////////////////////////////////////////////////////////

    class Patients extends MappedMethod {

        Map dataTableArgs = [idFieldName:'Id']

        String query = """
SELECT 
patients.id,
patients.birthdate AS birth_date,
patients.deathdate AS death_date,
patients.first AS first_name,
patients.last AS last_name,
patients.lat AS latitude,
patients.lon AS longitude 
FROM patients
WHERE patients.id IN
(
SELECT encounters.patient FROM encounters 
JOIN conditions ON encounters.id = conditions.encounter
WHERE conditions.description LIKE '%Viral sinusitis%'
GROUP BY encounters.patient HAVING COUNT(encounters.patient) > 1
)
"""

    }

    /** */
    class Encounters extends MappedMethod {

        Map dataTableArgs = [idFieldName:'encounter_id']

        String query = """\
SELECT 
encounters.id AS encounter_id, 
encounters.start, 
encounters.stop,
patients.id AS patient_id

FROM encounters
JOIN patients ON encounters.patient = patients.id
WHERE patients.id IN
(
SELECT encounters.patient FROM encounters
JOIN conditions ON encounters.id = conditions.encounter
WHERE conditions.description LIKE '%Viral sinusitis%'
)
LIMIT 10000
"""

    }

    class Conditions extends GenericMappedMethod {

        String query = """\
SELECT 
conditions.start,
conditions.stop AS end,
conditions.patient AS patient_id,
conditions.encounter AS encounter_id,
conditions.code,
conditions.description
FROM
conditions
WHERE description LIKE '%Viral sinusitis%'
LIMIT 10000
"""

    }

    class Medications extends GenericMappedMethod {

        String query = """\
SELECT 
medications.start,
medications.stop AS end,
medications.patient AS patient_id,
medications.encounter AS encounter_id,
medications.base_cost,
medications.dispenses,
medications.total_cost,
medications.code,
medications.description,
medications.reason_code,
medications.reason_description
FROM
medications
WHERE description LIKE '%Amoxicillin%'
LIMIT 10000
"""

    }


}