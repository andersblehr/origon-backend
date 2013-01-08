package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "memberKey"}, ignoreUnknown = true)
public class OMembership extends OReplicatedEntity
{
    public @IgnoreSave(IfFalse.class) boolean isActive = false;
    public @IgnoreSave(IfFalse.class) boolean isAdmin = false;
    public @IgnoreSave(IfNull.class) String contactRole;
    public @IgnoreSave(IfNull.class) String contactType;
    
    public @IgnoreSave Map<String, String> origoRef;
    
    public @IgnoreSave OMember member;
    public @IgnoreSave Map<String, String> memberRef;
    public Key<OMember> memberKey;
    

    public OMembership()
    {
        super();
    }
}
