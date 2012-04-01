package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey", "sharedEntityKey"}, ignoreUnknown=true)
public class ScSharedEntityRef extends ScCachedEntity
{
    public Key<ScCachedEntity> sharedEntityKey;
    public @NotSaved String sharedEntityId;

    
    public ScSharedEntityRef() {}
    
    
    @Override
    public void internaliseRelationships()
    {
        sharedEntityKey = new Key<ScCachedEntity>(ScCachedEntity.class, sharedEntityId);
        
        super.internaliseRelationships();
    }
    
    
    @Override
    public void externaliseRelationships()
    {
        sharedEntityId = sharedEntityKey.getRaw().getName();
        
        super.externaliseRelationships();
    }
}
