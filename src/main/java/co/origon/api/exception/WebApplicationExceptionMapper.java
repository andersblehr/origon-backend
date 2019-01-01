package co.origon.api.exception;

import co.origon.api.common.BasicAuthCredentials;
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
        Session.dispose();
        BasicAuthCredentials.dispose();
        final Status status = Status.fromStatusCode(e.getResponse().getStatus());
        if (status != Status.NOT_FOUND) {
            Session.log(Level.WARNING, e.getResponse().getStatus() + ": " + e.getMessage());
        }
        if (status == Status.INTERNAL_SERVER_ERROR) {
            e.printStackTrace();
        }
        return e.getResponse();
    }
}
