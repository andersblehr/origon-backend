package co.origon.api.filters;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.Config;
import co.origon.api.common.Config.Category;
import co.origon.api.common.Config.Setting;
import co.origon.api.common.Session;

import javax.annotation.Priority;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(1)
public class ThreadLocalDisposalFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        BasicAuthCredentials.dispose();
        Session.dispose();
    }
}
