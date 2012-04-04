package com.scolaapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey"}, ignoreUnknown=true)
public class ScMessageBoard extends ScCachedEntity
{
    public @NotSaved(IfDefault.class) boolean isAdmin = false;
    public @NotSaved({IfNull.class, IfEmptyString.class}) String roleRestriction;
    public String title;
    
    public @NotSaved Map<String, String> scolaRef;
    
    
    public ScMessageBoard()
    {
        super();
    }
}
