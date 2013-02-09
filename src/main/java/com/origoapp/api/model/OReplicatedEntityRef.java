package com.origoapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "referencedEntityKey"}, ignoreUnknown = true)
public class OReplicatedEntityRef extends OReplicatedEntity
{
    public @IgnoreSave String memberProxyId;
    public @IgnoreSave String referencedEntityId;
    public @IgnoreSave String referencedEntityOrigoId;
    public Key<OReplicatedEntity> referencedEntityKey;


    public OReplicatedEntityRef()
    {
        super();
    }
    
    
    @OnSave
    @Override
    public void internaliseRelationships()
    {
        super.internaliseRelationships();
        
        referencedEntityKey = Key.create(Key.create(OOrigo.class, referencedEntityOrigoId), OReplicatedEntity.class, referencedEntityId);
    }
    
    
    @OnLoad
    @Override
    public void externaliseRelationships()
    {
        super.externaliseRelationships();
        
        referencedEntityId = referencedEntityKey.getRaw().getName();
        referencedEntityOrigoId = referencedEntityKey.getParent().getRaw().getName();
    }
}
