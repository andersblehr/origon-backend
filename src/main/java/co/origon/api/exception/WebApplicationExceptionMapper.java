package co.origon.api.exception;

import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  private static final Logger LOG = Logger.getLogger(WebApplicationExceptionMapper.class.getName());

  public Response toResponse(WebApplicationException e) {
    final Status status = Status.fromStatusCode(e.getResponse().getStatus());
    if (status != Status.NOT_FOUND) {
      LOG.warning(e.getResponse().getStatus() + ": " + e.getMessage());
    }
    if (status == Status.INTERNAL_SERVER_ERROR) {
      e.printStackTrace();
    }
    return e.getResponse();
  }
}
