package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(value={"guardedScolaKey", "guardianScolaKey"}, ignoreUnknown=true)
public class ScScola extends ScCachedEntity
{
    public @NotSaved({IfNull.class, IfEmptyString.class}) String descriptionText;
    public String name;
    
    public Key<ScScola> guardedScolaKey;
    public Key<ScScola> guardianScolaKey;
    
    public @NotSaved ScScola guardedScola;
    public @NotSaved ScScola guardianScola;
    
    
    public ScScola()
    {
        super();
    }
}
