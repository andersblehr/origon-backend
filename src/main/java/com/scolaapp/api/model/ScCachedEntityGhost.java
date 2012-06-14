package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"scolaKey"}, ignoreUnknown = true)
public class ScCachedEntityGhost extends ScCachedEntity
{
    public @NotSaved String ghostedEntityClass;
    public @NotSaved boolean hasExpired = false;
    
    
    public ScCachedEntityGhost()
    {
        super();
    }
}
