package com.scolaapp.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScDevice")
public class ScDevice extends ScCachedEntity
{
    public String deviceName;
    public @Id String deviceUUID;
    
    
    public ScDevice()
    {
        super();
    }
    
    
    public ScDevice(String name, String UUID)
    {
        super();
        
        deviceName = name;
        deviceUUID = UUID;
    }
}
