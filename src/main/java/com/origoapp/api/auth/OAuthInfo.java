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
    public @Id String email;
    
    //public String deviceId; // TODO: Delete if not needed
    public String passwordHash;
    public String activationCode;
    
    public boolean isListed;
    public boolean didRegister;
    
    
    public OAuthInfo() {}
    
    
    public OAuthInfo(String email, /*String deviceId,*/ String passwordHash, String activationCode)
    {
        this.email = email;
        //this.deviceId = deviceId;
        this.passwordHash = passwordHash;
        this.activationCode = activationCode;
    }
}
