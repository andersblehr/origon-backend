package co.origon.api.exceptions;

import co.origon.api.common.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    public Response toResponse(WebApplicationException e) {
        if (Status.fromStatusCode(e.getResponse().getStatus()) != Status.NOT_FOUND) {
            Session.log(Level.WARNING, e.getResponse().getStatus() + ": " + e.getMessage());
        }

        return e.getResponse();
    }
}
