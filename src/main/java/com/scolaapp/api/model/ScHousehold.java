package com.scolaapp.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScHousehold")
public class ScHousehold extends ScCachedEntity
{
    public @Id Long id;
    public String addressLine1;
    public String addressLine2;
    public String postCodeAndCity;
    
    //public Key<ScEvent>[] events;
    public Key<ScPerson>[] residents;
    public Key<ScPerson>[] partTimeResidents;
    
    
    public ScHousehold()
    {
        super();
    }
}
