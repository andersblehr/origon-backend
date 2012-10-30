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
@JsonIgnoreProperties(value = {"origoKey", "linkedEntityKey"}, ignoreUnknown = true)
public class OLinkedEntityRef extends OReplicatedEntity
{
    public @NotSaved String linkedEntityId;
    public @NotSaved String linkedEntityOrigoId;
    public Key<OReplicatedEntity> linkedEntityKey;


    public OLinkedEntityRef()
    {
        super();
    }
    
    
    @PrePersist
    @Override
    public void internaliseRelationships()
    {
        super.internaliseRelationships();
        
        linkedEntityKey = new Key<OReplicatedEntity>(new Key<OOrigo>(OOrigo.class, linkedEntityOrigoId), OReplicatedEntity.class, linkedEntityId);
    }
    
    
    @PostLoad
    @Override
    public void externaliseRelationships()
    {
        super.externaliseRelationships();
        
        linkedEntityId = linkedEntityKey.getRaw().getName();
        linkedEntityOrigoId = linkedEntityKey.getParent().getRaw().getName();
    }
}
