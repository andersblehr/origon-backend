package com.origoapp.api.model;

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
@JsonIgnoreProperties(value = {"origoKey", "sharedEntityKey"}, ignoreUnknown = true)
public class OSharedEntityRef extends OCachedEntity
{
    public @NotSaved String sharedEntityId;
    public @NotSaved String sharedEntityOrigoId;
    public Key<OCachedEntity> sharedEntityKey;


    public OSharedEntityRef()
    {
        super();
    }
    
    
    @PrePersist
    @Override
    public void internaliseRelationships()
    {
        super.internaliseRelationships();
        
        sharedEntityKey = new Key<OCachedEntity>(new Key<OOrigo>(OOrigo.class, sharedEntityOrigoId), OCachedEntity.class, sharedEntityId);
    }
    
    
    @PostLoad
    @Override
    public void externaliseRelationships()
    {
        super.externaliseRelationships();
        
        sharedEntityId = sharedEntityKey.getRaw().getName();
        sharedEntityOrigoId = sharedEntityKey.getParent().getRaw().getName();
    }
}
