package com.scolaapp.api.auth;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;


@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScAuthState")
public class ScAuthState
{
    public @Id String userEmail;
    public String userFullName;
    public String passwordHash;
    public String deviceUUID;
    public String scolaShortname;
    public String registrationCode;
    
    public boolean isListed;
    public boolean isActive;
    
    
    public ScAuthState()
    {
    }
}
