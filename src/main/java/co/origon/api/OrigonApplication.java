package co.origon.api;

import co.origon.api.auth.AuthController;
import co.origon.api.config.ConfigController;
import co.origon.api.model.ReplicationController;

import org.glassfish.jersey.server.ResourceConfig;


public class OrigonApplication extends ResourceConfig
{
    public OrigonApplication()
    {
        register(new ConfigController());
        register(new AuthController());
        register(new ReplicationController());
    }
}
