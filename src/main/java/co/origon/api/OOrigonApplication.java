package co.origon.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import co.origon.api.auth.OAuthHandler;
import co.origon.api.model.OModelHandler;


public class OOrigonApplication extends Application
{
    private Set<Object> singletons = new HashSet<Object>();
    
    
    public OOrigonApplication()
    {
        singletons.add(new OAuthHandler());
        singletons.add(new OModelHandler());
    }
    
    
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
