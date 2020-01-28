package co.origon.api;

import co.origon.api.controller.AuthController;
import co.origon.api.controller.ConfigController;
import co.origon.api.controller.ReplicationController;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.ofy.DaoFactoryOfy;
import co.origon.api.repository.MemberProxyRepository;
import co.origon.api.repository.MembershipRepository;
import co.origon.api.repository.OrigoRepository;
import co.origon.api.service.ReplicationService;
import co.origon.api.common.Mailer;
import com.sun.tools.corba.se.idl.constExpr.Or;
import javax.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class OrigonApplication extends ResourceConfig {

  public OrigonApplication() {
    register(AuthController.class);
    register(ConfigController.class);
    register(ReplicationController.class);
    register(ReplicationService.class);
    register(MemberProxyRepository.class);
    register(MembershipRepository.class);
    register(OrigoRepository.class);

    register(
        new AbstractBinder() {
          @Override
          protected void configure() {
            bindAsContract(AuthController.class).in(Singleton.class);
            bindAsContract(ConfigController.class).in(Singleton.class);
            bindAsContract(ReplicationController.class).in(Singleton.class);
            bindAsContract(ReplicationService.class).in(Singleton.class);
            bindAsContract(MemberProxyRepository.class).in(Singleton.class);
            bindAsContract(MembershipRepository.class).in(Singleton.class);
            bindAsContract(OrigoRepository.class).in(Singleton.class);

            bindAsContract(DaoFactoryOfy.class).to(DaoFactory.class).in(Singleton.class);
          }
        });
  }
}
