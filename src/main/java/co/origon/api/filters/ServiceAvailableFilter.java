package co.origon.api.filters;

import co.origon.api.config.Config;
import co.origon.api.config.Config.Category;
import co.origon.api.config.Config.Setting;

import javax.annotation.Priority;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class ServiceAvailableFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String status;

        try {
            status = Config.get(Category.SYSTEM).getString(Setting.STATUS);
        } catch (Exception e) {
            throw new ServiceUnavailableException("System status unavailable: " + e.getMessage());
        }

        if (status == null || !status.equals(Setting.STATUS_OK)) {
            throw new ServiceUnavailableException("Service unavailable. System status: " + status);
        }
    }
}
