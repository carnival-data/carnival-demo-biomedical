package example.carnival.micronaut.config


import groovy.util.logging.Slf4j
import groovy.transform.ToString

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.ConfigurationBuilder

import carnival.core.CarnivalNeo4jConfiguration


@Slf4j
@ToString(includeNames=true) 
@ConfigurationProperties("carnival-micronaut")
class AppConfig {
    
    String name

    @ConfigurationProperties("carnival")
    static class Carnival {
        CarnivalNeo4jConfiguration neo4j
    }
    Carnival carnival = new Carnival()

    @ConfigurationProperties("vine")
    static class Vine {
        @ConfigurationProperties("example-db-vine")
        static class ExampleDbVine {
            String mode
            String directory
            Boolean directoryCreateIfNotPresent
        }
        ExampleDbVine exampleDbVine = new ExampleDbVine()
    }
    Vine vine = new Vine()

    @ConfigurationProperties("example-db")
    static class ExampleDb {
        String server
        Integer port
        String user
        String password
        String databaseName
    }
    ExampleDb exampleDb = new ExampleDb()

}