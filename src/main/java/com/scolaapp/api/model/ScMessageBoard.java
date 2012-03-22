package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(value={"scolaKey", "sharedEntity", "referenceToSharedEntity"}, ignoreUnknown=true)
public class ScMessageBoard extends ScCachedEntity
{
    public @NotSaved(IfFalse.class) boolean isAdmin = false;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String roleRestriction;
    public String title;
    
    
    public ScMessageBoard()
    {
        super();
    }
}
