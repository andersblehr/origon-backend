package com.scolaapp.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.scolaapp.api.strings.ScStringHandler;
import com.scolaapp.api.auth.ScAuthHandler;
import com.scolaapp.api.model.ScModelHandler;


public class ScScolaApplication extends Application
{
    private Set<Object> singletons = new HashSet<Object>();
    
    
    public ScScolaApplication()
    {
        singletons.add(new ScScolaHandler());
        singletons.add(new ScStringHandler());
        singletons.add(new ScAuthHandler());
        singletons.add(new ScModelHandler());
    }
    
    
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
