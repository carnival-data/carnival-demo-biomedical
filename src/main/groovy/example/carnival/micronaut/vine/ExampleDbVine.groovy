package example.carnival.micronaut.vine



import java.nio.file.Path
import java.nio.file.Paths

import groovy.transform.ToString
import groovy.util.logging.Slf4j
import groovy.sql.Sql
import javax.inject.Singleton
import javax.inject.Inject

import carnival.util.GenericDataTable
import carnival.util.MappedDataTable
import carnival.vine.Vine
import carnival.vine.VineConfiguration
import carnival.vine.CacheMode
import carnival.vine.MappedDataTableVineMethod
import carnival.vine.GenericDataTableVineMethod

import example.carnival.micronaut.config.AppConfig



@ToString(includeNames=true)
@Slf4j 
@Singleton
class ExampleDbVine implements Vine { 

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    /** injected by Micronaut via the ExampleDbVine constructor */
    AppConfig config


    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /** 
     * Utlity method to create a VineConfiguration object from the application 
     * config.  A default VineConfiguration object is used as the base object
     * to return.  Configuration elements are pulled from the application cofig
     * to set the analogous elements in the VineConfiguration object that will
     * be returned.
     *
     */
    VineConfiguration exampleVineConfiguration() {
        log.trace "exampleVineConfiguration()"
        def vconf = VineConfiguration.defaultConfiguration()
        if (config.vine.exampleDbVine.mode) vconf.cache.mode = CacheMode.valueOf(config.vine.exampleDbVine.mode)
        if (config.vine.exampleDbVine.directory) vconf.cache.directory = Paths.get(config.vine.exampleDbVine.directory)
        if (config.vine.exampleDbVine.directoryCreateIfNotPresent != null) vconf.cache.directoryCreateIfNotPresent = config.vine.exampleDbVine.directoryCreateIfNotPresent
        log.trace "vconf: " + vconf
        return vconf
    }


    /**
     * Constrtuct an ExampleDbVine.  
     * Inject an AppConfig.  
     * Set the vineConfiguration propertry to the VineConfiguration returned
     * by exampleVineConfiguration().  vineConfiguration comes from 
     * carnival.vine.Vine, the superclass of ExampleDbVine.  The Carnival
     * machinery will pass vineConfiguration to the vine methods defined in
     * this class (Patients, Encounters, etc.) when they are invoked.
     *
     */
    public ExampleDbVine(AppConfig appConfig) {
        this.config = appConfig
        this.vineConfiguration = exampleVineConfiguration()
    }


    ///////////////////////////////////////////////////////////////////////////
    // UTILITY
    ///////////////////////////////////////////////////////////////////////////

    /** create a Sql connection */
    Sql connect() {
        log.trace "jdbc:postgresql://${config.exampleDb.server}:${config.exampleDb.port}/${config.exampleDb.databaseName}"
        Sql.newInstance(
            driver: "org.postgresql.Driver",
            url: "jdbc:postgresql://${config.exampleDb.server}:${config.exampleDb.port}/${config.exampleDb.databaseName}",
            user: config.exampleDb.user,
            password: config.exampleDb.password
        )
    }


    /**
     * An abstract class for a vine method that returns a MappedDataTable.
     * Implementing classes need to define the query and the args that will
     * be used to create the output data table, which must include an
     * idFieldName.
     *
     * This is a convenience class to remove boilerplate.  It is not necessary
     * to define a abstract parent class to implement a vine method.
     *
     */
    abstract class MappedMethod extends MappedDataTableVineMethod { 
        
        abstract String getQuery()
        abstract Map getDataTableArgs()
        
        MappedDataTable fetch(Map args) {
            log.trace "MappedMethod.fetch vineConfiguration: ${vineConfiguration}"

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


    /**
     * An abstract class for a vine method that returns a GenericDataTable.
     * Implementing classes need to define the query only.
     *
     * This is a convenience class to remove boilerplate.  It is not necessary
     * to define a abstract parent class to implement a vine method.
     *
     */
    abstract class GenericMethod extends GenericDataTableVineMethod { 
        
        abstract String getQuery()

        GenericDataTable fetch(Map args) {
            log.trace "GenericMethod.fetch vineConfiguration: ${vineConfiguration}"

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

    /** get all patients */
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
--LIMIT 100
"""

    }


    /** get all encounters */
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
--LIMIT 100
"""

    }


    /** get all conditions */
    class Conditions extends GenericMethod {

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
--LIMIT 100
"""

    }


    /** get all medications */
    class Medications extends GenericMethod {

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
--LIMIT 100
"""

    }

}