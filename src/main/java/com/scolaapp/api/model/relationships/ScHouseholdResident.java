package com.scolaapp.api.model.relationships;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;

import com.scolaapp.api.model.ScHousehold;
import com.scolaapp.api.model.ScScolaMember;


@Entity
@Cached(expirationSeconds=600)
public class ScHouseholdResident
{
    public @Id Long id;
    
    public Key<ScHousehold> householdKey;
    public Key<ScScolaMember> memberKey;

    
    public ScHouseholdResident() {}
    
    
    public ScHouseholdResident(Key<ScHousehold> householdKey, Key<ScScolaMember> memberKey)
    {
        this.householdKey = householdKey;
        this.memberKey = memberKey;
    }
}
