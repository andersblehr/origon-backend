package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(value={"memberKey", "scolaKey"}, ignoreUnknown=true)
public class ScSharedEntityRef extends ScCachedEntity
{
    public Key<ScCachedEntity> sharedEntityKey;
    public @NotSaved String sharedEntityId;

    
    public ScSharedEntityRef() {}
}
