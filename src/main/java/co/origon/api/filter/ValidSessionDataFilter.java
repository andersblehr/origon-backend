package co.origon.api.filter;

import co.origon.api.common.Session;
import co.origon.api.common.UrlParams;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.DeviceCredentials;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@ValidSessionData
@Priority(4)
public class ValidSessionDataFilter implements ContainerRequestFilter {

    private DaoFactory daoFactory;

    @Inject
    ValidSessionDataFilter(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String deviceToken = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_TOKEN);
        final String appVersion = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.APP_VERSION);
        String deviceId = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_ID);
        String deviceType = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_TYPE);

        if (isSet(deviceToken) && !isSet(deviceId) && !isSet(deviceType)) {
            final DeviceCredentials deviceCredentials = daoFactory.daoFor(DeviceCredentials.class).get(deviceToken);
            if (deviceCredentials == null)
                throw new BadRequestException("Incomplete session data and unknown device token");
            deviceId = deviceCredentials.deviceId();
            deviceType = deviceCredentials.deviceType();
        }

        try {
            Session.create(deviceId, deviceType, appVersion);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid session data", e);
        }
    }

    private boolean isSet(String string) {
        return string != null && string.length() > 0;
    }
}
