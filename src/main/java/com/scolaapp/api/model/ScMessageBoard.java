package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScMessageBoard extends ScCachedEntity
{
    public String title;
    
    public Key<ScScola> scolaKey;
    public @NotSaved ScScola scola;
    
    
    public ScMessageBoard()
    {
        super();
    }
}
