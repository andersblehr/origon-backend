package com.scolaapp.api.auth;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds = 600)
public class ScAuthInfo
{
    public @Id String userId;
    
    public String homeScolaId;
    public String deviceId;
    public String passwordHash;
    public String registrationCode;
    
    public boolean isListed;
    public boolean didRegister;
    
    
    public ScAuthInfo() {}
}
