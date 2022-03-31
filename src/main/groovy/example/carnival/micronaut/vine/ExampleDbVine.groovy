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


    ///////////////////////////////////////////////////////////////////////////
    // VINE METHODS
    ///////////////////////////////////////////////////////////////////////////


    /** */
    class Encounters extends MappedMethod {

        Map dataTableArgs = [idFieldName:'encounter_id']

        String query = """\
SELECT 
encounters.id AS encounter_id, 
encounters.start, 
encounters.stop,
patients.id AS patient_id,
patients.birthdate,
patients.deathdate,
patients.first,
patients.last,
patients.lat,
patients.lon

FROM encounters
JOIN patients ON encounters.patient = patients.id
LIMIT 10
"""

    }

//     /** */
//     class Conditions extends MappedMethod {

//         Map dataTableArgs = [idFieldName:'start']

//         String query = """\
// SELECT 
// conditions.start, 
// conditions.stop,
// conditions.patient as patient_id,
// conditions.encounter as encounter_id,
// conditions.code,
// conditions.description
// FROM conditions
// LIMIT 10
// """

//     }

    /** */
    class Careplans extends MappedMethod {

        Map dataTableArgs = [idFieldName:'careplan_id']

        String query = """\
SELECT 
careplans.id as careplan_id,
careplans.start, 
careplans.stop,
careplans.patient as patient_id,
careplans.encounter as encounter_id,
careplans.code,
careplans.description
FROM careplans
LIMIT 10
"""

    }

}