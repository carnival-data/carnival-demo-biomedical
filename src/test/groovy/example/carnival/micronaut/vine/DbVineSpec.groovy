package example.carnival.micronaut.vine



import java.text.ParseException
import javax.inject.Inject
import spock.lang.Specification
import spock.lang.Shared
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest



@MicronautTest
class DbVineSpec extends Specification {

    
    @Shared @Inject ExampleDbVine exampleDbVine


    def setupSpec() {}
    def setup() {}
    def cleanup() {}
    def cleanupSpec() {}


    ///////////////////////////////////////////////////////////////////////////
    // TESTS
    ///////////////////////////////////////////////////////////////////////////
/*
    void "test query"() {
        when:
        def res = exampleDbVine.method('MyMappedMethod').call().result
        println "res: ${res}"

        then:
        res != null
    }
*/
}