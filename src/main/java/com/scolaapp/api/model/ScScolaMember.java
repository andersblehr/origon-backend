package com.scolaapp.api.model;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;



@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey", "primaryResidenceKey"}, ignoreUnknown=true)
public class ScScolaMember extends ScCachedEntity
{
    public String name;
    public String gender;
    public Date dateOfBirth;
    public String mobilePhone;
    
    public String passwordHash;
    public Date activeSince;
    
    public @NotSaved(IfDefault.class) boolean didRegister = false;
    public @NotSaved(IfDefault.class) boolean isMinor = false;
    
    public @NotSaved ScHousehold primaryResidence;
    public @NotSaved Map<String, String> primaryResidenceRef;
    public Key<ScHousehold> primaryResidenceKey;
    
    
    public ScScolaMember()
    {
        super();
    }
}
