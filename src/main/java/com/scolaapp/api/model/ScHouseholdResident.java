package com.scolaapp.api.model;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;


@Entity
@Cached(expirationSeconds=600)
public class ScHouseholdResident
{
    public @Id Long id;
    public Key<ScHousehold> household;
    public Key<ScPerson> resident;

    
    public ScHouseholdResident() {}
    
    
    public ScHouseholdResident(Key<ScHousehold> household_, Key<ScPerson> resident_)
    {
        household = household_;
        resident = resident_;
    }
}
