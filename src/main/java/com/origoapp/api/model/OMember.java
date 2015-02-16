package com.origoapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

//import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey"}, ignoreUnknown = true)
public class OMember extends OReplicatedEntity
{
    public String name;
    public String gender;
    public @IgnoreSave(IfNull.class) Date dateOfBirth;
    public @IgnoreSave(IfNull.class) String mobilePhone;
    public @IgnoreSave(IfNull.class) String email;
    //public @IgnoreSave(IfNull.class) Blob photo;
    public @IgnoreSave(IfNull.class) String settings;
    
    public @IgnoreSave(IfNull.class) boolean isMinor;
    public @IgnoreSave(IfNull.class) String fatherId;
    public @IgnoreSave(IfNull.class) String motherId;
    
    public @IgnoreSave(IfNull.class) String createdIn;
    public @IgnoreSave(IfNull.class) Date activeSince;
    
    
    public OMember()
    {
        super();
    }
    
    
    @JsonIgnore
    public String getProxyId()
    {
        return hasEmail() ? email : entityId;
    }
    
    
    public boolean hasEmail()
    {
        return email != null && email.length() > 0;
    }
}
