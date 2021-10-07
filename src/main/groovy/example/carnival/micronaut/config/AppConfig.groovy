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


    @ConfigurationProperties("example-database")
    static class ExampleDatabase {
        String server
        Integer port
        String user
        String password
    }
    ExampleDatabase exampleDatabase = new ExampleDatabase()


    @ConfigurationProperties("graphql-service")
    static class GraphqlService {

        @ConfigurationProperties("api")
        static class Api {
            String token
        }
        Api api = new Api()

    }
    GraphqlService graphqlService = new GraphqlService()

}