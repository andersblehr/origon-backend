package com.scolaapp.api.model.proxy;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import com.scolaapp.api.model.ScSharedEntityRef;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScSharedEntityProxy
{
    public @Id String sharedEntityId;
    
    public Set<Key<ScSharedEntityRef>> sharedEntityRefKeys;
    
    
    public ScSharedEntityProxy()
    {
        sharedEntityRefKeys = new HashSet<Key<ScSharedEntityRef>>();
    }
}
