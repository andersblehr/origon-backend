package com.scolaapp.api.model.proxy;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.PostLoad;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfEmpty;
import com.googlecode.objectify.condition.IfNull;

import com.scolaapp.api.auth.ScAuthMeta;
import com.scolaapp.api.model.ScMember;
import com.scolaapp.api.model.ScMembership;
import com.scolaapp.api.model.ScScola;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScMemberProxy
{
    public @Id String userId;
    public String scolaId;
    
    public @NotSaved(IfDefault.class) boolean didRegister = false;
    public @NotSaved(IfNull.class) String passwordHash;
    
    public @NotSaved Key<ScScola> scolaKey;
    public @NotSaved Key<ScMember> memberKey;
    
    public @NotSaved(IfEmpty.class) Set<Key<ScMembership>> membershipKeys;
    public @NotSaved(IfEmpty.class) Set<Key<ScAuthMeta>> authMetaKeys;
    
    
    public ScMemberProxy() {}
    
    
    public ScMemberProxy(String userId, String scolaId)
    {
        this.userId = userId;
        this.scolaId = scolaId;
        
        scolaKey = new Key<ScScola>(ScScola.class, scolaId);
        memberKey = new Key<ScMember>(scolaKey, ScMember.class, userId);
        
        membershipKeys = new HashSet<Key<ScMembership>>();
        authMetaKeys = new HashSet<Key<ScAuthMeta>>();
    }
    
    
    @PostLoad
    public void populateNonSavedValues()
    {
        scolaKey = new Key<ScScola>(ScScola.class, scolaId);
        memberKey = new Key<ScMember>(scolaKey, ScMember.class, userId);
        
        if (membershipKeys == null) {
            membershipKeys = new HashSet<Key<ScMembership>>();
        }
        
        if (authMetaKeys == null) {
            authMetaKeys = new HashSet<Key<ScAuthMeta>>();
        }
    }
}
