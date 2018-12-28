package co.origon.api.exceptions;

import co.origon.api.common.Session;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    public Response toResponse(IllegalArgumentException e) {
        Session.log(Level.WARNING, "Illegal argument: " + e.getMessage());

        return Response
                .status(Status.BAD_REQUEST)
                .build();
    }
}
