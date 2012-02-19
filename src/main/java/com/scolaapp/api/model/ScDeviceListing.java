package com.scolaapp.api.model;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;


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
