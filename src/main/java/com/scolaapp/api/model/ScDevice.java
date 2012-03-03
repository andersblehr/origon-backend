package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScDevice extends ScCachedEntity
{
    public @NotSaved String uuid;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String type;
    
    
    public ScDevice()
    {
        super();
        
        uuid = entityId;
    }
}
