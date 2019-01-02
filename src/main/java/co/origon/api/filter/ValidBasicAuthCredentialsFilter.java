package co.origon.api.filter;

import co.origon.api.annotation.ValidBasicAuthCredentials;
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

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            BasicAuthCredentials.validate(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid basic auth credentials", e);
        }
    }
}
