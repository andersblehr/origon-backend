package co.origon.api.filter;

import co.origon.api.common.Settings;
import com.typesafe.config.Config;
import javax.annotation.Priority;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class ServiceAvailableFilter implements ContainerRequestFilter {

  private Config systemConfig;

  public ServiceAvailableFilter() {
    this.systemConfig = Settings.system();
  }

  ServiceAvailableFilter(Config systemConfig) {
    this.systemConfig = systemConfig;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    final String status;
    try {
      status = systemConfig.getString(Settings.SYSTEM_STATUS);
    } catch (Exception e) {
      throw new ServiceUnavailableException("System status unavailable: " + e.getMessage());
    }
    if (status == null || !status.equals(Settings.SYSTEM_STATUS_OK)) {
      throw new ServiceUnavailableException("Service unavailable. System status: " + status);
    }
  }
}
