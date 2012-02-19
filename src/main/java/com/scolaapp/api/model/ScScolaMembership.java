package com.scolaapp.api.model;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;


@Entity
@Cached(expirationSeconds=600)
public class ScScolaMembership
{
    public @Id Long id;
    public Key<ScScolaMember> member;
    public Key<ScScola> scola;

    
    public ScScolaMembership() {}
    
    
    public ScScolaMembership(Key<ScScolaMember> member_, Key<ScScola> scola_)
    {
        member = member_;
        scola = scola_;
    }
}
