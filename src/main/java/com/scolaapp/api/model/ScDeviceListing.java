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
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScDeviceListing extends ScCachedEntity
{
    public String displayName;
    
    public Key<ScDevice> deviceKey;
    public Key<ScScolaMember> memberKey;

    public @NotSaved ScDevice device;
    public @NotSaved ScScolaMember member;
    
    
    public ScDeviceListing() {}
    
    
    public ScDeviceListing(Key<ScDevice> deviceKey, Key<ScScolaMember> memberKey)
    {
        this.deviceKey = deviceKey;
        this.memberKey = memberKey;
    }
}
