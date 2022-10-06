package example.carnival.micronaut



import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import io.micronaut.runtime.Micronaut
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.context.annotation.ConfigurationProperties

import example.carnival.micronaut.config.AppConfig



@CompileStatic
@Slf4j
class Application {

    /** */
    static void main(String[] args) {

        // start the application
        ApplicationContext ctx = Micronaut.run(Application, args)

        // validate configuration
        //ctx.getBean(AppConfig).errors().ifPresent { errs ->
        //    log.error "configuration erroes: $errs"
        //    log.error "stopping application..."
        //    ctx.stop()
        //}
    }
}
