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
    public Key<ScDevice> device;
    public Key<ScScolaMember> usedBy;

    
    public ScDeviceListing() {}
    
    
    public ScDeviceListing(Key<ScDevice> device_, Key<ScScolaMember> usedBy_)
    {
        device = device_;
        usedBy = usedBy_;
    }
}
