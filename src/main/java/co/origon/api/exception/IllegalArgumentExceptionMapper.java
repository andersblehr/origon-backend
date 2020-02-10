package co.origon.api.exception;

import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

  private static final Logger LOG =
      Logger.getLogger(IllegalArgumentExceptionMapper.class.getName());

  public Response toResponse(IllegalArgumentException e) {
    LOG.warning("Illegal argument: " + e.getMessage());
    return Response.status(Status.BAD_REQUEST).build();
  }
}
