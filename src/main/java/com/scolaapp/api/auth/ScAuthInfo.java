package com.scolaapp.api.auth;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

import com.scolaapp.api.model.ScPerson;


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
    public boolean isActive;
    public boolean isAuthenticated;
    public boolean isDeviceListed;
    
    public ScPerson listedPerson;
    
    
    public ScAuthInfo() {}
}
