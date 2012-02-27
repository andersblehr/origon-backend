package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScDevice extends ScCachedEntity
{
    public String name;
    public String uuid;
    
    
    public ScDevice()
    {
        super();
    }
    
    
    public ScDevice(String deviceName, String deviceUUID)
    {
        super();
        
        name = deviceName;
        uuid = deviceUUID;
    }
}
