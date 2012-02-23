package com.scolaapp.api.model.relationships;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.scolaapp.api.model.ScPerson;
import com.scolaapp.api.model.ScScola;


@Entity
@Cached(expirationSeconds=600)
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
