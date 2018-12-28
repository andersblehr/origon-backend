package co.origon.api.filters;

import co.origon.api.annotations.SessionDataValidated;
import co.origon.api.common.Session;
import co.origon.api.common.UrlParams;
import co.origon.api.entities.OAuthMeta;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@SessionDataValidated
@Priority(4)
public class SessionDataValidatedFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String authToken = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.AUTH_TOKEN);
        final String appVersion = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.APP_VERSION);
        String deviceId = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_ID);
        String deviceType = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_TYPE);

        if (authToken != null && deviceId == null && deviceType == null) {
            final OAuthMeta authMeta = OAuthMeta.get(authToken);
            deviceId = authMeta.getDeviceId();
            deviceType = authMeta.getDeviceType();
        }

        try {
            Session.create(deviceId, deviceType, appVersion);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid session data", e);
        }
    }
}
