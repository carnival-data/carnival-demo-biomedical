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
        log.trace "jdbc:postgresql://${config.exampleDb.server}:${config.exampleDb.port}/${config.exampleDb.databaseName}"
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
LIMIT 10
"""

    }

    class Conditions extends MappedMethod {

        Map dataTableArgs = [idFieldName:'Condition_id']

        String query = """
SELECT 
patient || '-' || encounter || '-' || cond.rownum AS condition_id,
cond.start,
cond.stop AS end,
cond.patient AS patient_id,
cond.encounter AS encounter_id,
cond.code,
cond.description
FROM
(
    SELECT 
    patient || '-' || encounter AS condition_id,
    start,
    stop,
    patient,
    encounter,
    code,
    description
    ,
    ROW_NUMBER() OVER() AS rownum
    from conditions
) AS cond
LIMIT 10
"""

    }

    /** */
    class Encounters extends MappedMethod {

        Map dataTableArgs = [idFieldName:'Encounter_id']

        String query = """
SELECT 
encounters.id AS encounter_id, 
encounters.start, 
encounters.stop
FROM encounters
LIMIT 10
"""

    }

}