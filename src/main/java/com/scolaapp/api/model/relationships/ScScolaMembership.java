package com.scolaapp.api.model.relationships;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScScolaMember;


@Entity
@Cached(expirationSeconds=600)
public class ScScolaMembership
{
    public @Id Long id;
    public boolean isAdmin;
    
    public Key<ScScolaMember> member;
    public Key<ScScola> scola;

    
    public ScScolaMembership() {}
    
    
    public ScScolaMembership(Key<ScScolaMember> memberKey, Key<ScScola> scolaKey, boolean isAdmin)
    {
        member = memberKey;
        scola = scolaKey;
        
        this.isAdmin = isAdmin;
    }
}
