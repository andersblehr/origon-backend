package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(value="memberKey", ignoreUnknown=true)
public class ScScolaMembership extends ScCachedEntity
{
    public @NotSaved(IfFalse.class) boolean isActive = false;
    public @NotSaved(IfFalse.class) boolean isAdmin = false;
    public @NotSaved(IfFalse.class) boolean isCoach = false;
    public @NotSaved(IfFalse.class) boolean isTeacher = false;
    
    public Key<ScScolaMember> memberKey;
    public @NotSaved ScScolaMember member;

    
    public ScScolaMembership() {}
    
    
    public ScScolaMembership(Key<ScScolaMember> memberKey, Key<ScScola> scolaKey, boolean isAdmin)
    {
        this.memberKey = memberKey;
        this.scolaKey = scolaKey;
        
        this.isAdmin = isAdmin;
    }
}
