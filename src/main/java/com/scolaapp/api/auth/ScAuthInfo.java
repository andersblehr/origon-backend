package com.scolaapp.api.auth;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;


@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScAuthInfo")
public class ScAuthInfo
{
    public @Id String email;
    public String name;
    public String passwordHash;
    public String deviceUUIDHash;
    public String scolaShortname;
    public String registrationCode;
    
    public boolean isListed;
    public boolean isActive;
    
    
    public ScAuthInfo() {}
}
