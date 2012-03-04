package com.scolaapp.api.auth;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;

import com.scolaapp.api.model.ScHousehold;
import com.scolaapp.api.model.ScScolaMember;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScAuthInfo
{
    public @Id String email;
    
    public String name;
    public String passwordHash;
    public String deviceUUID;
    public String registrationCode;
    
    public boolean isListed;
    public boolean isRegistered;
    public boolean isAuthenticated;
    
    public @NotSaved ScScolaMember member;
    public @NotSaved ScHousehold household;
    
    
    public ScAuthInfo() {}
}
