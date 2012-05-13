package com.scolaapp.api.model;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"scolaKey"}, ignoreUnknown = true)
public class ScSharedEntityRef extends ScCachedEntity
{
    public @NotSaved String sharedEntityId;
    public @NotSaved String sharedEntityScolaId;
    public Key<ScCachedEntity> sharedEntityKey;


    public ScSharedEntityRef()
    {
        super();
    }
    
    
    @PrePersist
    @Override
    public void internaliseRelationships()
    {
        super.internaliseRelationships();
        
        sharedEntityKey = new Key<ScCachedEntity>(new Key<ScScola>(ScScola.class, sharedEntityScolaId), ScCachedEntity.class, sharedEntityId);
    }
    
    
    @PostLoad
    @Override
    public void externaliseRelationships()
    {
        super.externaliseRelationships();
        
        sharedEntityId = sharedEntityKey.getRaw().getName();
        sharedEntityScolaId = sharedEntityKey.getParent().getRaw().getName();
    }
}
