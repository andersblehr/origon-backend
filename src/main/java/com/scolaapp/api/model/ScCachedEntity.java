package com.scolaapp.api.model;

import java.util.Date;

import com.googlecode.objectify.annotation.Unindexed;


@Unindexed
public abstract class ScCachedEntity
{
    public Date dateCreated;
    public Date dateExpires;
    public Date dateModified;
    
    
    public ScCachedEntity()
    {
        dateCreated = new Date();
        dateModified = dateCreated;
    }
}
