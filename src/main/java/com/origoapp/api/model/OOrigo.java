package com.origoapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

//import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNotNull;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "parentOrigoKey"}, ignoreUnknown = true)
public class OOrigo extends OReplicatedEntity
{
    public @IgnoreSave(IfNull.class) String name;
    public String type;
    
    public @IgnoreSave(IfNull.class) String descriptionText;
    public @IgnoreSave(IfNull.class) String address;
    public @IgnoreSave(IfNull.class) String location;
    public @IgnoreSave(IfNull.class) String telephone;
    //public @IgnoreSave(IfNull.class) Blob photo;
    public @IgnoreSave(IfNull.class) String permissions;
    public @IgnoreSave(IfFalse.class) boolean isForMinors;
    
    public @IgnoreSave(IfNull.class) String joinCode;
    public @Index(IfNotNull.class) @IgnoreSave(IfNull.class) String internalJoinCode;

    
    public OOrigo()
    {
        super();
    }
}
