package com.scolaapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfNull;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"scolaKey", "parentScolaKey"}, ignoreUnknown = true)
public class ScScola extends ScCachedEntity
{
    public String name;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String descriptionText;
    
    public @NotSaved({IfNull.class, IfEmptyString.class}) String addressLine1;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String addressLine2;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String postCodeAndCity;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String phoneNumber;
    
    public @NotSaved ScScola parentScola;
    public @NotSaved Map<String, String> parentScolaRef;
    public @NotSaved(IfNull.class) Key<ScScola> parentScolaKey;


    public ScScola()
    {
        super();
    }
}
