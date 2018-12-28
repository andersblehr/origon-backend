package co.origon.api.filters;

import co.origon.api.annotations.TokenAuthenticated;
import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.UrlParams;
import co.origon.api.entities.OAuthMeta;
import co.origon.api.entities.OMemberProxy;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import java.util.Date;

@Provider
@TokenAuthenticated
@Priority(3)
public class TokenAuthenticatedFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String authToken = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.AUTH_TOKEN);
        final OAuthMeta authMeta;
        try {
            authMeta = OAuthMeta.get(authToken);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new BadRequestException("Missing or invalid query parameter: " + UrlParams.AUTH_TOKEN, e);
        }
        if (authMeta == null) {
            throw new BadRequestException("Cannot authenticate unknown auth token");
        }
        if (authMeta.getDateExpires().before(new Date())) {
            throw new NotAuthorizedException("Auth token has expired");
        }

        final OMemberProxy userProxy = OMemberProxy.get(authMeta.getEmail());
        if (!userProxy.didRegister()) {
            throw new BadRequestException("Cannot authenticate unknown or inactive user " + authMeta.getEmail());
        }

        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        if (credentials != null && !credentials.getEmail().equals(authMeta.getEmail())) {
            throw new BadRequestException("Basic auth credentials do not match records for auth token provided");
        }
    }
}
