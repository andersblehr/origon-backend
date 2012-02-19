package com.scolaapp.api.model;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScScola extends ScCachedEntity
{
    public @Id Long id;
    public String name;
    public String descriptionText;
    
    public Key<ScScolaMember>[] membersActive;
    public Key<ScPerson>[] membersInactive;

    
    public ScScola()
    {
        super();
    }
}
