package co.origon.api;

import co.origon.api.controller.AuthController;
import co.origon.api.controller.ConfigController;
import co.origon.api.controller.ReplicationController;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.ofy.DaoFactoryOfy;
import co.origon.api.service.ReplicationService;
import co.origon.mailer.api.Mailer;
import co.origon.mailer.origon.OrigonMailer;
import javax.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class OrigonApplication extends ResourceConfig {

  public OrigonApplication() {
    register(AuthController.class);
    register(ConfigController.class);
    register(ReplicationController.class);
    register(ReplicationService.class);

    register(
        new AbstractBinder() {
          @Override
          protected void configure() {
            bindAsContract(AuthController.class).in(Singleton.class);
            bindAsContract(ConfigController.class).in(Singleton.class);
            bindAsContract(ReplicationController.class).in(Singleton.class);
            bindAsContract(ReplicationService.class).in(Singleton.class);

            bindAsContract(DaoFactoryOfy.class).to(DaoFactory.class).in(Singleton.class);
            bindAsContract(OrigonMailer.class).to(Mailer.class).in(Singleton.class);
          }
        });
  }
}
