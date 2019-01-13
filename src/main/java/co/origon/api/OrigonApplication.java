package co.origon.api;

import co.origon.api.controller.AuthController;
import co.origon.api.controller.ConfigController;
import co.origon.api.controller.ReplicationController;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.ofy.DaoFactoryOfy;
import co.origon.mailer.api.Mailer;
import co.origon.mailer.origon.OrigonMailer;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;

public class OrigonApplication extends ResourceConfig {

    public OrigonApplication() {
        register(new AuthController());
        register(new ConfigController());
        register(new ReplicationController());

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(DaoFactoryOfy.class).to(DaoFactory.class).in(Singleton.class);
                bindAsContract(OrigonMailer.class).to(Mailer.class).in(Singleton.class);
            }
        });
    }
}
