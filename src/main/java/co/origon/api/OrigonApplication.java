package co.origon.api;

import co.origon.api.common.Mailer;
import co.origon.api.controller.AuthController;
import co.origon.api.controller.ConfigController;
import co.origon.api.controller.ReplicationController;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.ofy.DaoFactoryOfy;
import co.origon.api.repository.ofy.RepositoryFactoryOfy;
import co.origon.api.service.ReplicationService;
import javax.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class OrigonApplication extends ResourceConfig {

  public OrigonApplication() {

    register(
        new AbstractBinder() {
          @Override
          protected void configure() {
            bindAsContract(AuthController.class);
            bindAsContract(ConfigController.class);
            bindAsContract(ReplicationController.class);

            bindAsContract(ReplicationService.class);

            bindAsContract(RepositoryFactoryOfy.class).to(RepositoryFactoryOfy.class);
            bindAsContract(DaoFactoryOfy.class).to(DaoFactory.class).in(Singleton.class);

            bindAsContract(Mailer.class);
          }
        });
  }
}
