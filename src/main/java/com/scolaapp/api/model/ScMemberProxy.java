package com.scolaapp.api.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.PostLoad;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import com.scolaapp.api.auth.ScAuthTokenMeta;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScMemberProxy
{
    public @Id String userId;
    public String homeScolaId;
    
    public Key<ScMember> memberKey;
    public Key<ScScola> homeScolaKey;
    
    public Set<Key<ScMembership>> membershipKeySet;
    public Set<Key<ScAuthTokenMeta>> authMetaKeySet;
    
    
    public ScMemberProxy() {}
    
    
    public ScMemberProxy(String userId, String homeScolaId)
    {
        this.userId = userId;
        this.homeScolaId = homeScolaId;
        
        homeScolaKey = new Key<ScScola>(ScScola.class, homeScolaId);
        memberKey = new Key<ScMember>(homeScolaKey, ScMember.class, userId);
        
        membershipKeySet = new HashSet<Key<ScMembership>>();
        authMetaKeySet = new HashSet<Key<ScAuthTokenMeta>>();
    }
    
    
    @PostLoad
    public void instantiateNullKeySets()
    {
        if (membershipKeySet == null) {
            membershipKeySet = new HashSet<Key<ScMembership>>();
        }
    }
}
