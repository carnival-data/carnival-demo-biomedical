package example.carnival.micronaut.vine



import javax.inject.Singleton
import javax.inject.Inject

import groovy.transform.ToString
import groovy.util.logging.Slf4j
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
class DatabaseVine implements Vine { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject AppConfig config


    ///////////////////////////////////////////////////////////////////////////
    // UTILITY
    ///////////////////////////////////////////////////////////////////////////

    Sql connect() {
        Sql.newInstance(
            driver: 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
            url: "jdbc:sqlserver://${config.database.server}:${config.database.port};databaseName=TheDatabaseName;",
            user: config.database.user,
            password: config.database.password
        )
    }


    class MyQuery extends MappedDataTableVineMethod { 

        MappedDataTable fetch(Map args) {
            log.trace "database connect()"
            def sql = connect()

            def mdt = createDataTable(idFieldName:'ID')

        String query = """\
SELECT SomeTable.ID, SomeTable.*
FROM SomeTable
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