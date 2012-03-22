package com.scolaapp.api.auth;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScAuthInfo
{
    public @Id String deviceId;
    
    public String name;
    public String email;
    public String passwordHash;
    public String registrationCode;
    
    public boolean isListed;
    public boolean isRegistered;
    public boolean isAuthenticated;
    
    
    public ScAuthInfo() {}
}
