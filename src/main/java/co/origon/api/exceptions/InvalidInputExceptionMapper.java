package co.origon.api.exceptions;

import co.origon.api.OrigonApplication;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;


@Provider
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException> {

    private static final Logger LOG = Logger.getLogger(OrigonApplication.class.getName());

    public Response toResponse(InvalidInputException e) {
        LOG.warning(e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
