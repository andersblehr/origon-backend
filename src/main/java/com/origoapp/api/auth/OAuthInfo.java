package com.origoapp.api.auth;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds = 600)
public class OAuthInfo
{
    public @Id String userId;
    
    public String deviceId;
    public String passwordHash;
    public String activationCode;
    
    public boolean isListed;
    public boolean didRegister;
    
    
    public OAuthInfo() {}
    
    
    public OAuthInfo(String userId, String deviceId, String passwordHash, String activationCode)
    {
        this.userId = userId;
        this.deviceId = deviceId;
        this.passwordHash = passwordHash;
        this.activationCode = activationCode;
    }
}
