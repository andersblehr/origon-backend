package co.origon.api.filter;

import co.origon.api.annotation.TokenAuthenticated;
import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.UrlParams;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;

import javax.annotation.Priority;
import javax.inject.Inject;
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

    private DaoFactory daoFactory;

    @Inject
    TokenAuthenticatedFilter(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String deviceToken = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_TOKEN);
        if (deviceToken == null || deviceToken.length() != 40) {
            throw new BadRequestException("Missing or invalid query parameter: " + UrlParams.DEVICE_TOKEN);
        }

        final DeviceCredentials deviceCredentials = daoFactory.daoFor(DeviceCredentials.class).get(deviceToken);
        if (deviceCredentials == null) {
            throw new BadRequestException("Cannot authenticate unknown auth token");
        }
        if (deviceCredentials.dateExpires().before(new Date())) {
            throw new NotAuthorizedException("Auth token has expired");
        }

        final MemberProxy userProxy = daoFactory.daoFor(MemberProxy.class).get(deviceCredentials.email());
        if (!userProxy.isRegistered()) {
            throw new BadRequestException("Cannot authenticate unknown or inactive user " + deviceCredentials.email());
        }

        final BasicAuthCredentials basicAuthCredentials = BasicAuthCredentials.getCredentials();
        if (basicAuthCredentials != null && !basicAuthCredentials.getEmail().equals(deviceCredentials.email())) {
            throw new BadRequestException("Basic auth credentials do not match records for auth token provided");
        }
    }
}
