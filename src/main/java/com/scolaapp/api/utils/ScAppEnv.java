package com.scolaapp.api.utils;

import java.util.logging.Logger;

import com.googlecode.objectify.*;

import com.scolaapp.api.ScScolaApplication;
import com.scolaapp.api.auth.ScAuthState;
import com.scolaapp.api.model.ScPerson;

public class ScAppEnv
{
    private static final Logger log = Logger.getLogger(ScScolaApplication.class.getName());

    private static ScAppEnv env = null;
    private static Objectify objectify;
    
    
    static
    {
        ObjectifyService.register(ScAuthState.class);
        ObjectifyService.register(ScPerson.class);
        
        objectify = ObjectifyService.begin();
    }
    
    
    protected ScAppEnv()
    {
    }
    
    
    public static Logger getLog()
    {
        return log;
    }

    
    
    public static ScAppEnv getEnv()
    {
        if (null == env) {
            env = new ScAppEnv();
        }
        
        return env;
    }
    
    
    public static Objectify ofy()
    {
        return objectify;
    }
}
