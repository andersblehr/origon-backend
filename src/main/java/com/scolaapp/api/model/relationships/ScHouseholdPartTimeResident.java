package com.scolaapp.api.model.relationships;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.scolaapp.api.model.ScHousehold;
import com.scolaapp.api.model.ScPerson;


@Entity
@Cached(expirationSeconds=600)
public class ScHouseholdPartTimeResident
{
    public @Id Long id;
    public Key<ScHousehold> household;
    public Key<ScPerson> partTimeResident;

    
    public ScHouseholdPartTimeResident() {}
    
    
    public ScHouseholdPartTimeResident(Key<ScHousehold> household_, Key<ScPerson> partTimeResident_)
    {
        household = household_;
        partTimeResident = partTimeResident_;
    }
}
