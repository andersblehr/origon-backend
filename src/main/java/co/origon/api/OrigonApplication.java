package co.origon.api;

import co.origon.api.controller.AuthController;
import co.origon.api.controller.ConfigController;
import co.origon.api.controller.ReplicationController;

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
