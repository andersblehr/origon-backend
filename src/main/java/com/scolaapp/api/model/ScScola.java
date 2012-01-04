package com.scolaapp.api.model;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;


@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScScola")
public class ScScola
{
    public @Id String shortname;
    public String name;
    public String descriptionText;
    public Key<ScPerson>[] members;

    
    public ScScola() {}
}
