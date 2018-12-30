package co.origon.api.filter;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.Session;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class ThreadLocalDisposalFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Session.dispose();
        BasicAuthCredentials.dispose();
    }
}
