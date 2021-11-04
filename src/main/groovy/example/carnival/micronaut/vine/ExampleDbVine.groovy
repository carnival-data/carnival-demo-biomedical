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
            driver: 'org.postgresql.Driver',
            url: "jdbc:postgresql://${config.exampleDb.server}:${config.exampleDb.port}/EHR;",
            user: config.exampleDb.user,
            password: config.exampleDb.password
        )
    }


    class MyMappedMethod extends MappedDataTableVineMethod { 

        MappedDataTable fetch(Map args) {
            log.trace "database connect()"
            def sql = connect()

            def mdt = createDataTable(idFieldName:'id')

            String query = """\
  SELECT *
  FROM [EHR].[encounters]
  """

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


}