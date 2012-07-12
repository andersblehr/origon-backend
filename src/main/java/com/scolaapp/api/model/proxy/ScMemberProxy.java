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
import com.scolaapp.api.model.ScMemberResidency;
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
    
    public @NotSaved(IfEmpty.class) Set<Key<ScAuthMeta>> authMetaKeys;
    public @NotSaved(IfEmpty.class) Set<Key<ScMembership>> membershipKeys;

    public @NotSaved Key<ScScola> scolaKey;
    public @NotSaved Key<ScMember> memberKey;
    public @NotSaved Set<Key<ScMemberResidency>> residencyKeys;
    public @NotSaved Set<Key<ScScola>> residenceKeys;
    
    
    public ScMemberProxy() {}
    
    
    public ScMemberProxy(String userId, String scolaId)
    {
        this.userId = userId;
        this.scolaId = scolaId;
        
        scolaKey = new Key<ScScola>(ScScola.class, scolaId);
        memberKey = new Key<ScMember>(scolaKey, ScMember.class, userId);
        
        authMetaKeys = new HashSet<Key<ScAuthMeta>>();
        membershipKeys = new HashSet<Key<ScMembership>>();
    }
    
    
    @PostLoad
    public void populateNonSavedValues()
    {
        scolaKey = new Key<ScScola>(ScScola.class, scolaId);
        memberKey = new Key<ScMember>(scolaKey, ScMember.class, userId);
        
        if (authMetaKeys == null) {
            authMetaKeys = new HashSet<Key<ScAuthMeta>>();
        }
        
        if (membershipKeys == null) {
            membershipKeys = new HashSet<Key<ScMembership>>();
        }
        
        residencyKeys = new HashSet<Key<ScMemberResidency>>();
        residenceKeys = new HashSet<Key<ScScola>>();
        
        for (Key<ScMembership> membershipKey : membershipKeys) {
            Key<ScScola> scolaKey = new Key<ScScola>(ScScola.class, membershipKey.getRaw().getParent().getName());
            String membershipId = membershipKey.getRaw().getName();
            
            if (membershipId.startsWith(userId)) {
                residencyKeys.add(new Key<ScMemberResidency>(scolaKey, ScMemberResidency.class, membershipId));
                residenceKeys.add(new Key<ScScola>(scolaKey, ScScola.class, membershipId.substring(membershipId.indexOf("$") + 1)));
            }
        }
    }
}
