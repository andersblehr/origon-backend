package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "memberKey"}, ignoreUnknown = true)
public class OMembership extends OCachedEntity
{
    public @NotSaved(IfFalse.class) boolean isActive = false;
    public @NotSaved(IfFalse.class) boolean isAdmin = false;
    public @NotSaved(IfFalse.class) boolean isCoach = false;
    public @NotSaved(IfFalse.class) boolean isTeacher = false;
    
    public @NotSaved Map<String, String> origoRef;
    
    public @NotSaved OMember member;
    public @NotSaved Map<String, String> memberRef;
    public Key<OMember> memberKey;
    

    public OMembership()
    {
        super();
    }
}
