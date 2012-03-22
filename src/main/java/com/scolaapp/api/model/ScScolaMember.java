package com.scolaapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;



@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(value={"scolaKey", "primaryResidenceKey", "sharedEntity", "referenceToSharedEntity"}, ignoreUnknown=true)
public class ScScolaMember extends ScCachedEntity
{
    public String name;
    public String email;
    public String mobilePhone;
    public String gender;
    public Date dateOfBirth;
    
    public String passwordHash;
    public Date activeSince;
    
    public @NotSaved(IfFalse.class) boolean didRegister;
    public @NotSaved(IfFalse.class) boolean isMinor;
    
    public Key<ScHousehold> primaryResidenceKey;
    public @NotSaved ScHousehold primaryResidence;
    
    
    public ScScolaMember()
    {
        super();
    }
}
