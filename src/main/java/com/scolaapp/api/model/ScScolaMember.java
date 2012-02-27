package com.scolaapp.api.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScScolaMember extends ScPerson
{
    public Date memberSince;
    public String passwordHash;
    
    public @NotSaved List<ScScola> adminMemberships;
    public @NotSaved List<ScDevice> devices;
    public @NotSaved List<ScScola> memberships;
    
    
    public ScScolaMember()
    {
        super();
    }
}
