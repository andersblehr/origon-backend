package com.scolaapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;



@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScScolaMember extends ScCachedEntity
{
    public Date activeSince;
    public Date dateOfBirth;
    public String email;
    public String gender;
    public @NotSaved(IfFalse.class) boolean isMinor = false;
    public @NotSaved(IfFalse.class) boolean isRegistered = false;
    public String mobilePhone;
    public String name;
    public String passwordHash;
    
    
    public ScScolaMember()
    {
        super();
    }
}
