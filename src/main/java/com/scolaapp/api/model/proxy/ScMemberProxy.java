package com.scolaapp.api.model.proxy;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.PostLoad;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

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
    public String homeScolaId;
    
    public Key<ScMember> memberKey;
    public Key<ScScola> homeScolaKey;
    
    public Set<Key<ScMembership>> membershipKeys;
    public Set<Key<ScAuthMeta>> authMetaKeys;
    
    
    public ScMemberProxy() {}
    
    
    public ScMemberProxy(String userId, String homeScolaId)
    {
        this.userId = userId;
        this.homeScolaId = homeScolaId;
        
        homeScolaKey = new Key<ScScola>(ScScola.class, homeScolaId);
        memberKey = new Key<ScMember>(homeScolaKey, ScMember.class, userId);
        
        membershipKeys = new HashSet<Key<ScMembership>>();
        authMetaKeys = new HashSet<Key<ScAuthMeta>>();
    }
    
    
    @PostLoad
    public void instantiateNullKeySets()
    {
        if (membershipKeys == null) {
            membershipKeys = new HashSet<Key<ScMembership>>();
        }
        
        if (authMetaKeys == null) {
            authMetaKeys = new HashSet<Key<ScAuthMeta>>();
        }
    }
}
