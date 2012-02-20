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
public class ScHousehold extends ScCachedEntity
{
    public @Id Long id;
    public String addressLine1;
    public String addressLine2;
    public String postCodeAndCity;
        
    
    public ScHousehold()
    {
        super();
    }
}
