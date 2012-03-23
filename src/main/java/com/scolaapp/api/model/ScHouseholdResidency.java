package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfEmptyString;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey", "householdKey", "residentKey"}, ignoreUnknown=true)
public class ScHouseholdResidency extends ScCachedEntity
{
    public @NotSaved(IfDefault.class) int daysAtATime = 0;
    public @NotSaved(IfDefault.class) boolean isPartTime = false;
    public @NotSaved({IfDefault.class, IfEmptyString.class}) String name = null;
    public @NotSaved(IfDefault.class) boolean presentOn01Jan = true;
    public @NotSaved(IfDefault.class) int switchDay = 0;
    public @NotSaved(IfDefault.class) int switchFrequency = 0;
    
    public Key<ScHousehold> householdKey;
    public Key<ScScolaMember> residentKey;
    
    public @NotSaved ScHousehold household;
    public @NotSaved ScScolaMember resident;

    
    public ScHouseholdResidency() {}
    
    
    public ScHouseholdResidency(Key<ScHousehold> householdKey, Key<ScScolaMember> residentKey)
    {
        this.householdKey = householdKey;
        this.residentKey = residentKey;
    }
}
