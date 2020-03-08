package example.carnival.micronaut



import groovy.transform.CompileStatic
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.MediaType



@CompileStatic
@Controller("/hello") 
class HelloController {

    @Get("/") 
    @Produces(MediaType.TEXT_PLAIN) 
    String index() {
        "Hello World" 
    }

}