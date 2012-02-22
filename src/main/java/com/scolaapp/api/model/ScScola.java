package com.scolaapp.api.model;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScScola extends ScCachedEntity
{
    public @Id String entityId;
    public String name;
    public String descriptionText;
    
    
    public ScScola()
    {
        super();
    }
}
