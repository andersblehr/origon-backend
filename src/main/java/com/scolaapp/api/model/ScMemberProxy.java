package com.scolaapp.api.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;

import com.scolaapp.api.auth.ScAuthToken;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScMemberProxy
{
    public @Id String userId;
    public Key<ScCachedEntity> memberKey;
    
    public Key<ScCachedEntity>[] residenceKeys;
    public Key<ScCachedEntity>[] membershipKeys;
    public Key<ScAuthToken>[] authTokenKeys;
    
    public @NotSaved Set<Key<ScCachedEntity>> residenceKeySet;
    public @NotSaved Set<Key<ScCachedEntity>> membershipKeySet;
    public @NotSaved Set<Key<ScAuthToken>> authTokenKeySet;
    
    
    public ScMemberProxy() {}
    
    
    public ScMemberProxy(String userId, Key<ScScola> scolaKey, Iterable<Key<ScAuthToken>> tokenKeyIterable)
    {
        this.userId = userId;
        this.memberKey = new Key<ScCachedEntity>(scolaKey, ScCachedEntity.class, userId);
        
        residenceKeySet = new HashSet<Key<ScCachedEntity>>();
        membershipKeySet = new HashSet<Key<ScCachedEntity>>();
        authTokenKeySet = new HashSet<Key<ScAuthToken>>();
        
        Iterator<Key<ScAuthToken>> tokenKeyIterator = tokenKeyIterable.iterator();
        
        while (tokenKeyIterator.hasNext()) {
            authTokenKeySet.add(tokenKeyIterator.next());
        }
    }
    
    
    public void internaliseKeySets()
    {
        residenceKeys = residenceKeySet.toArray(residenceKeys);
        membershipKeys = membershipKeySet.toArray(membershipKeys);
        authTokenKeys = authTokenKeySet.toArray(authTokenKeys);
    }
    
    
    public void externaliseKeySets()
    {
        residenceKeySet = new HashSet<Key<ScCachedEntity>>(Arrays.asList(residenceKeys));
        membershipKeySet = new HashSet<Key<ScCachedEntity>>(Arrays.asList(membershipKeys));
        authTokenKeySet = new HashSet<Key<ScAuthToken>>(Arrays.asList(authTokenKeys));
    }
}
