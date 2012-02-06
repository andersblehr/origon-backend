package com.scolaapp.api.model;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;


@Entity
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScScolaListing")
public class ScScolaListing
{
    public @Id Long id;
    public Key<ScPerson> person;
    public Key<ScScola> scola;

    
    public ScScolaListing() {}
    
    
    public ScScolaListing(Key<ScPerson> person_, Key<ScScola> scola_)
    {
        person = person_;
        scola = scola_;
    }
}
