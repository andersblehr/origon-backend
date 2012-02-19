package com.scolaapp.api.model;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;


@Entity
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScHouseholdPartTimeResident")
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
