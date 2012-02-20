package com.scolaapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
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
    
    
    public ScScolaMember()
    {
        super();
    }
    
    
    public ScScolaMember(ScPerson newMember, String passwordHash_)
    {
        super(newMember);
        
        memberSince = new Date();
        passwordHash = passwordHash_;
    }
}
