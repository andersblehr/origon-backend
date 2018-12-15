package co.origon.api;

import co.origon.api.controllers.AuthController;
import co.origon.api.controllers.ConfigController;
import co.origon.api.controllers.ReplicationController;

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
