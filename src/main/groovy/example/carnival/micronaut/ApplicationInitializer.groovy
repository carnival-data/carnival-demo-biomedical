package example.carnival.micronaut



import javax.inject.Inject
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import groovy.util.logging.Slf4j
import groovy.transform.ToString
import io.micronaut.context.annotation.Context
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.context.annotation.Requires
import carnival.core.graph.CoreGraphNeo4j

import example.carnival.micronaut.config.AppConfig
import example.carnival.micronaut.service.SyncService



/** */
@ToString(includeNames=true)
@Slf4j 
@Context
@Requires(notEnv="test")
class ApplicationInitializer implements AutoCloseable {

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    @Inject ApplicationContext applicationContext
    @Inject AppConfig config

    @Inject SyncService syncService


    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////


    @PostConstruct 
    void initialize() {
        log.info "${this.class.simpleName} initialize()"

        // environment.getActiveNames().contains("test")
        Environment env = applicationContext.getEnvironment()


        syncService.syncExample()

        // exit if we're in a test environment
        if (env.getActiveNames().contains("test")) return
    }
    

    @PreDestroy 
    @Override
    void close() throws Exception {
        log.info "${this.class.simpleName} close()"
    } 

}

