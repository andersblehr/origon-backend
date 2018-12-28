package co.origon.api.exceptions;

import co.origon.api.OrigonApplication;
import co.origon.api.common.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOG = Logger.getLogger(OrigonApplication.class.getName());

    public Response toResponse(WebApplicationException e) {
        if (Status.fromStatusCode(e.getResponse().getStatus()) != Status.NOT_FOUND) {
            final String message = e.getResponse().getStatus() + ": " + e.getMessage();
            if (Session.getSession() != null) {
                Session.log(Level.WARNING, message);
            } else {
                Session.LOGGER.warning(message);
            }
        }

        return e.getResponse();
    }
}
