package com.scolaapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScHousehold extends ScCachedEntity
{
    public String addressLine1;
    public String addressLine2;
    public String postCodeAndCity;
        
    
    public ScHousehold()
    {
        super();
    }
}
