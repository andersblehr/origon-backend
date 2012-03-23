package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey"}, ignoreUnknown=true)
public class ScHousehold extends ScCachedEntity
{
    public @NotSaved({IfNull.class, IfEmptyString.class}) String name;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String addressLine1;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String addressLine2;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String postCodeAndCity;
        
    
    public ScHousehold()
    {
        super();
    }
}
