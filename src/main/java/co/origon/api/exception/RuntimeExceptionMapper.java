package co.origon.api.exception;

import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

  private static final Logger LOG = Logger.getLogger(RuntimeExceptionMapper.class.getName());

  public Response toResponse(RuntimeException e) {
    LOG.severe("Runtime exception: " + e.getMessage());
    e.printStackTrace();
    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
  }
}
