package co.origon.api.filter;

import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.Config;
import co.origon.api.model.api.entity.Config.Category;
import co.origon.api.model.api.entity.Config.Setting;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class ServiceAvailableFilter implements ContainerRequestFilter {

  private DaoFactory daoFactory;

  @Inject
  ServiceAvailableFilter(DaoFactory daoFactory) {
    this.daoFactory = daoFactory;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    final String status;
    try {
      status = daoFactory.daoFor(Config.class).get(Category.SYSTEM).getString(Setting.STATUS);
    } catch (Exception e) {
      throw new ServiceUnavailableException("System status unavailable: " + e.getMessage());
    }

    if (status == null || !status.equals(Setting.STATUS_OK))
      throw new ServiceUnavailableException("Service unavailable. System status: " + status);
  }
}
