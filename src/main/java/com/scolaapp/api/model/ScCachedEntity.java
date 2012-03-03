package com.scolaapp.api.model;

import java.util.Date;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfNull;


@Entity
@Unindexed
@JsonTypeInfo (
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "entityClass")
@JsonSubTypes({
    @Type(value = ScDevice.class, name = "ScDevice"),
    @Type(value = ScDeviceListing.class, name = "ScDeviceListing"),
    @Type(value = ScHousehold.class, name = "ScHousehold"),
    @Type(value = ScHouseholdResidency.class, name = "ScHouseholdResidency"),
    @Type(value = ScMessageBoard.class, name = "ScMessageBoard"),
    @Type(value = ScScola.class, name = "ScScola"),
    @Type(value = ScScolaMember.class, name = "ScScolaMember"),
    @Type(value = ScScolaMembership.class, name = "ScScolaMembership")})
public abstract class ScCachedEntity
{
    public @Id String entityId;
    
    public Date dateCreated;
    public @NotSaved(IfNull.class) Date dateExpires;
    public Date dateModified;
    
    
    public ScCachedEntity() {}
}
