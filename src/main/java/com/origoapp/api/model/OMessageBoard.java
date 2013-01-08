package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.condition.IfEmptyString;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey"}, ignoreUnknown = true)
public class OMessageBoard extends OReplicatedEntity
{
    public @IgnoreSave Map<String, String> origoRef;
    
    public @IgnoreSave(IfFalse.class) boolean isAdmin = false;
    public @IgnoreSave({IfNull.class, IfEmptyString.class}) String roleRestriction;
    public String title;
    
    
    public OMessageBoard()
    {
        super();
    }
}
