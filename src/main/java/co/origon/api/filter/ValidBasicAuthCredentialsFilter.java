package co.origon.api.filter;

import co.origon.api.common.BasicAuthCredentials;
import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

@Provider
@ValidBasicAuthCredentials
@Priority(2)
public class ValidBasicAuthCredentialsFilter implements ContainerRequestFilter {

  private static final String BASIC_AUTH_CREDENTIALS = "basic-auth-credentials";

  @Override
  public void filter(ContainerRequestContext requestContext) {
    try {
      requestContext.setProperty(
          BasicAuthCredentials.CONTEXT_KEY,
          new BasicAuthCredentials(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)));
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid basic auth credentials", e);
    }
  }
}
