package co.origon.api.exception;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.Session;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

  public Response toResponse(RuntimeException e) {
    Session.dispose();
    BasicAuthCredentials.dispose();
    Session.log(Level.SEVERE, "Runtime exception: " + e.getMessage());
    e.printStackTrace();
    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
  }
}
