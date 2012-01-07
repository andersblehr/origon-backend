package com.scolaapp.api.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScScolaMember")
public class ScScolaMember extends ScPerson {
    public Date memberSince;
    public String passwordHash;
    public Key<ScScola>[] scolas;
    
    
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
