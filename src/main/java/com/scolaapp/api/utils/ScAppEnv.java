package com.scolaapp.api.utils;


public class ScAppEnv
{
    private static ScAppEnv env = null;

    private String sessionHash;
    
    
    public static ScAppEnv env()
    {
        if (null == env) {
            env = new ScAppEnv();
        }
        
        return env;
    }
    
    
    public boolean isSessionValid(String sessionHash_)
    {
        return sessionHash.equals(sessionHash_);
    }
}
