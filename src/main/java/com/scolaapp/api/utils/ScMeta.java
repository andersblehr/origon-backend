package com.scolaapp.api.utils;

import javax.servlet.http.HttpServletResponse;


public class ScMeta
{
    public String userId;
    public String deviceId;
    
    private String deviceType;
    private String appVersion;
    
    
    public ScMeta(String deviceId, String deviceType, String appVersion)
    {
        this.userId = "<processing>";
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.appVersion = appVersion;
        
        if ((deviceId == null) || (deviceType == null) || (appVersion == null)) {
            ScLog.log().severe(this.meta() + String.format("Incomplete request [deviceId: %s; deviceType: %s; appVersion: %s], raising BAD_REQUEST (400).", deviceId, deviceType, appVersion));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    
    public String meta()
    {
        return String.format("[%s] %s/%s: ", deviceId.substring(0, 8), deviceType, appVersion);
    }
}
