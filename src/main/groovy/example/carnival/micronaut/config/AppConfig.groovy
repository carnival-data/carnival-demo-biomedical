package example.carnival.micronaut.config



import groovy.util.logging.Slf4j
import groovy.transform.ToString


import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.ConfigurationBuilder



@Slf4j
@ToString(includeNames=true) 
@ConfigurationProperties("carnival-micronaut")
class AppConfig {
    
    String name

    @ConfigurationProperties("example-db")
    static class ExampleDb {
        String server
        Integer port
        String user
        String password
        String databaseName
    }
    ExampleDb exampleDb = new ExampleDb()

    @ConfigurationProperties("sub-config")
    static class SubConfig {
        int someNumber
        String someString

        @ConfigurationProperties("sub-sub-config")
        static class SubSubConfig {
            String anotherString
        }
        SubSubConfig subSubConfig = new SubSubConfig()
    }
    SubConfig subConfig = new SubConfig()

}