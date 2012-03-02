package com.scolaapp.api.model.relationships;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.scolaapp.api.model.ScDevice;
import com.scolaapp.api.model.ScScolaMember;


@Entity
@Cached(expirationSeconds=600)
public class ScDeviceListing
{
    public @Id Long id;
    public String deviceDisplayName;
    
    public Key<ScDevice> deviceKey;
    public Key<ScScolaMember> memberKey;

    
    public ScDeviceListing() {}
    
    
    public ScDeviceListing(Key<ScDevice> deviceKey, Key<ScScolaMember> memberKey)
    {
        this.deviceKey = deviceKey;
        this.memberKey = memberKey;
    }
}
