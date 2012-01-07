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
@XmlRootElement(name="ScScola")
public class ScScola extends ScCachedEntity
{
    public @Id String shortname;
    public String name;
    public String descriptionText;
    public Key<ScPerson>[] members;

    
    public ScScola()
    {
        super();
    }
    
    
    public ScScola(String shortname_, Key<ScPerson> memberKey)
    {
        super();
        
        shortname = shortname_;
        members[0] = memberKey;
    }
}
