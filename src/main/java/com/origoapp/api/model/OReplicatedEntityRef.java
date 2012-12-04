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
@JsonIgnoreProperties(value = {"origoKey", "referencedEntityKey"}, ignoreUnknown = true)
public class OReplicatedEntityRef extends OReplicatedEntity
{
    public @NotSaved String referencedEntityId;
    public @NotSaved String referencedEntityOrigoId;
    public Key<OReplicatedEntity> referencedEntityKey;


    public OReplicatedEntityRef()
    {
        super();
    }
    
    
    @PrePersist
    @Override
    public void internaliseRelationships()
    {
        super.internaliseRelationships();
        
        referencedEntityKey = new Key<OReplicatedEntity>(new Key<OOrigo>(OOrigo.class, referencedEntityOrigoId), OReplicatedEntity.class, referencedEntityId);
    }
    
    
    @PostLoad
    @Override
    public void externaliseRelationships()
    {
        super.externaliseRelationships();
        
        referencedEntityId = referencedEntityKey.getRaw().getName();
        referencedEntityOrigoId = referencedEntityKey.getParent().getRaw().getName();
    }
}
