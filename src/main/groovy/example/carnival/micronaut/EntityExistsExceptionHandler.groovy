package example.carnival.micronaut


import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import javax.inject.Singleton


/**
 * An exception handler to return HttpStatus.CONFLICT when an entity already
 * exists in the data store.
 *
 */
@Produces
@Singleton 
@Requires(classes = [EntityExistsException.class, ExceptionHandler.class])  
public class EntityExistsExceptionHandler implements ExceptionHandler<EntityExistsException, HttpResponse> { 

    @Override
    public HttpResponse handle(HttpRequest request, EntityExistsException exception) {
        return HttpResponse.status(HttpStatus.CONFLICT, exception.message) 
    }
}