package com.origoapp.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.origoapp.api.auth.OAuthHandler;
import com.origoapp.api.model.OModelHandler;
import com.origoapp.api.strings.OStringHandler;


public class OOrigoApplication extends Application
{
    private Set<Object> singletons = new HashSet<Object>();
    
    
    public OOrigoApplication()
    {
        singletons.add(new OStringHandler());
        singletons.add(new OAuthHandler());
        singletons.add(new OModelHandler());
    }
    
    
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
