package com.origoapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.condition.IfNull;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey"}, ignoreUnknown = true)
public class OMember extends OReplicatedEntity
{
    public String name;
    public String gender;
    public Date dateOfBirth;
    public @IgnoreSave(IfNull.class) String givenName;
    public @IgnoreSave(IfNull.class) String mobilePhone;
    public @IgnoreSave(IfNull.class) String email;
    public @IgnoreSave(IfNull.class) Blob photo;
    public @IgnoreSave(IfNull.class) Date activeSince;
    public @IgnoreSave(IfNull.class) String passwordHash;
    
    
    public OMember()
    {
        super();
    }
}
