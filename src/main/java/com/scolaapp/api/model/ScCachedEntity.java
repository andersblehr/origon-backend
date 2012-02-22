package com.scolaapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.googlecode.objectify.annotation.Unindexed;


@Unindexed
@JsonTypeInfo (
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @Type(value = ScDevice.class, name = "ScDevice"),
    @Type(value = ScHousehold.class, name = "ScHousehold"),
    @Type(value = ScPerson.class, name = "ScPerson"),
    @Type(value = ScScola.class, name = "ScScola"),
    @Type(value = ScScolaMember.class, name = "ScScolaMember")})
public abstract class ScCachedEntity
{
    public Date dateCreated;
    public Date dateExpires;
    public Date dateModified;
    
    
    public ScCachedEntity()
    {
        Date now = new Date();
        
        dateCreated = now;
        dateModified = now;
    }
}
