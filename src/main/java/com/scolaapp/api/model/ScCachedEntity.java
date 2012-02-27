package com.scolaapp.api.model;

import java.util.Date;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;


@Unindexed
@JsonTypeInfo (
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "entityType")
@JsonSubTypes({
    @Type(value = ScDevice.class, name = "ScDevice"),
    @Type(value = ScHousehold.class, name = "ScHousehold"),
    @Type(value = ScMessageBoard.class, name = "ScMessageBoard"),
    @Type(value = ScPerson.class, name = "ScPerson"),
    @Type(value = ScScola.class, name = "ScScola"),
    @Type(value = ScScolaMember.class, name = "ScScolaMember")})
public abstract class ScCachedEntity
{
    public @Id String entityId;
    
    public Date dateCreated;
    public Date dateExpires;
    public Date dateModified;
    
    public @NotSaved boolean isDirty;
    
    
    public ScCachedEntity() {}
}
