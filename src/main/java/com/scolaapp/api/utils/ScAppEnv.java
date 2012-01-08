package com.scolaapp.api.utils;


public class ScAppEnv
{
    public String appVersion;
    public String deviceType;
    public String deviceUUID;
    
    
    protected ScAppEnv(String appVersion_, String deviceType_, String deviceUUID_)
    {
        appVersion = appVersion_;
        deviceType = deviceType_;
        deviceUUID = deviceUUID_;
    }
    
    
    public static ScAppEnv env(String appVersion, String deviceType, String deviceUUID)
    {
        return new ScAppEnv(appVersion, deviceType, deviceUUID);
    }
}
