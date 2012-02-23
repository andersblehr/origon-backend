package com.scolaapp.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScDevice extends ScCachedEntity
{
    public String name;
    public @Id String uuid;
    
    
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
