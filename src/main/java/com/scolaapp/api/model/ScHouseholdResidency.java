package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;
import com.googlecode.objectify.condition.IfZero;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(value={"householdKey", "residentKey"}, ignoreUnknown=true)
public class ScHouseholdResidency extends ScCachedEntity
{
    public @NotSaved(IfZero.class) int daysAtATime = 0;
    public @NotSaved(IfFalse.class) boolean isPartTime = false;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String name = null;
    public @NotSaved(IfDefault.class) boolean presentOn01Jan = true;
    public @NotSaved(IfZero.class) int switchDay = 0;
    public @NotSaved(IfZero.class) int switchFrequency = 0;
    
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
