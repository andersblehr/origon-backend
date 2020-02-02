package co.origon.api;

import co.origon.api.common.Mailer;
import co.origon.api.controller.AuthController;
import co.origon.api.controller.ReplicationController;
import co.origon.api.repository.RepositoryFactory;
import co.origon.api.repository.ofy.OfyRepositoryFactory;
import co.origon.api.service.AuthService;
import co.origon.api.service.ReplicationService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class OrigonApplication extends ResourceConfig {

  public OrigonApplication() {

    register(
        new AbstractBinder() {
          @Override
          protected void configure() {
            bindAsContract(AuthController.class);
            bindAsContract(ReplicationController.class);
            bindAsContract(AuthService.class);
            bindAsContract(ReplicationService.class);
            bindAsContract(Mailer.class);

            bindAsContract(OfyRepositoryFactory.class).to(RepositoryFactory.class);
          }
        });
  }
}
