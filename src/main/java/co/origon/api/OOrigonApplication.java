package co.origon.api;

import co.origon.api.auth.OAuthHandler;
import co.origon.api.config.ConfigController;
import co.origon.api.model.OModelHandler;

import org.glassfish.jersey.server.ResourceConfig;


public class OOrigonApplication extends ResourceConfig
{
    public OOrigonApplication()
    {
        register(new ConfigController());
        register(new OAuthHandler());
        register(new OModelHandler());
    }
}
