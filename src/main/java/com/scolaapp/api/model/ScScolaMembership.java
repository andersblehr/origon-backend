package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScScolaMembership extends ScCachedEntity
{
    public @Indexed @NotSaved(IfFalse.class) boolean isActive;
    public @Indexed @NotSaved(IfFalse.class) boolean isAdmin;
    public @Indexed @NotSaved(IfFalse.class) boolean isRole1;
    public @Indexed @NotSaved(IfFalse.class) boolean isRole2;
    public @Indexed @NotSaved(IfFalse.class) boolean isRole3;
    
    public String role1Label;
    public String role2Label;
    public String role3Label;
    
    public Key<ScScolaMember> memberKey;
    public Key<ScScola> scolaKey;
    
    public @NotSaved ScScolaMember member;
    public @NotSaved ScScola scola;

    
    public ScScolaMembership() {}
    
    
    public ScScolaMembership(Key<ScScolaMember> memberKey, Key<ScScola> scolaKey, boolean isAdmin)
    {
        this.memberKey = memberKey;
        this.scolaKey = scolaKey;
        
        this.isAdmin = isAdmin;
    }
}
