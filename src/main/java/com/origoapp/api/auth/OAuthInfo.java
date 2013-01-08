package com.origoapp.api.auth;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


@Entity
@Cache(expirationSeconds = 600)
public class OAuthInfo
{
    public @Id String email;
    
    public String passwordHash;
    public String activationCode;
    public boolean isListed;
    
    
    public OAuthInfo() {}
    
    
    public OAuthInfo(String email, String passwordHash, String activationCode)
    {
        this.email = email;
        this.passwordHash = passwordHash;
        this.activationCode = activationCode;
    }
}
