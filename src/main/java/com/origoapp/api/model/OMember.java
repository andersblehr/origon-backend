package com.origoapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey"}, ignoreUnknown = true)
public class OMember extends OCachedEntity
{
    public String name;
    public String gender;
    public Date dateOfBirth;
    public @NotSaved(IfNull.class) String givenName;
    public @NotSaved(IfNull.class) String mobilePhone;
    public @NotSaved(IfNull.class) Blob photo;
    
    public @NotSaved(IfFalse.class) boolean didRegister = false;
    public @NotSaved(IfNull.class) Date activeSince;
    public @NotSaved(IfNull.class) String passwordHash;
    
    
    public OMember()
    {
        super();
    }
}
