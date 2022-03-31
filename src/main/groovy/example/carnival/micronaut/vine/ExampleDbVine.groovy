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
--JOIN encounters ON encounters.patient = patients.id
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
encounters.stop,
patients.id AS patient_id  
FROM encounters
JOIN patients ON encounters.patient = patients.id
LIMIT 10
"""

    }

}