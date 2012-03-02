package com.scolaapp.api.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;



@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScScolaMember extends ScCachedEntity
{
    public Date activeSince;
    public Date dateOfBirth;
    public String email;
    public String gender;
    public Boolean isMinor;
    public Boolean isRegistered;
    public String mobilePhone;
    public String name;
    public String passwordHash;
    
    public Key<ScHousehold> householdKey;
    
    public @NotSaved ScHousehold household;
    
    public @NotSaved List<ScDevice> devices;
    public @NotSaved List<ScScolaMembership> scolaMemberships;
    public @NotSaved List<ScHousehold> partTimeHouseholds;
    
    
    public ScScolaMember()
    {
        super();
    }
}
