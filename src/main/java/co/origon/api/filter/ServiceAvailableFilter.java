package co.origon.api.filter;

import co.origon.api.common.Config;
import javax.annotation.Priority;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class ServiceAvailableFilter implements ContainerRequestFilter {

  private com.typesafe.config.Config systemConfig;

  public ServiceAvailableFilter() {
    this.systemConfig = Config.system();
  }

  ServiceAvailableFilter(com.typesafe.config.Config systemConfig) {
    this.systemConfig = systemConfig;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    final String status;
    try {
      status = systemConfig.getString(Config.SYSTEM_STATUS);
    } catch (Exception e) {
      throw new ServiceUnavailableException("System status unavailable: " + e.getMessage());
    }
    if (status == null || !status.equals(Config.SYSTEM_STATUS_OK)) {
      throw new ServiceUnavailableException("Service unavailable. System status: " + status);
    }
  }
}
