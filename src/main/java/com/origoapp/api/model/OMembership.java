package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "memberKey", "associateMemberKey"}, ignoreUnknown = true)
public class OMembership extends OReplicatedEntity
{
    public @IgnoreSave(IfFalse.class) boolean isAdmin = false;
    public @IgnoreSave(IfNull.class) String contactRole;
    public @IgnoreSave(IfNull.class) String contactType;
    
    public String type;
    public @IgnoreSave(IfNull.class) String status;
    
    public @IgnoreSave OMember member;
    public @IgnoreSave @EmbedMap Map<String, String> memberRef;
    public @IgnoreSave(IfNull.class) Key<OMember> memberKey;
    
    public @IgnoreSave @EmbedMap Map<String, String> origoRef;
    

    public OMembership()
    {
        super();
    }
    
    
    @JsonIgnore
    public boolean isFetchable()
    {
        boolean isFetchable = false;
        
        if (!isExpired) {
            isFetchable = type.equals("~") || type.equals("A");
            
            if (!isFetchable && (status != null)) {
                isFetchable = status.equals("I") || status.equals("A");
            }
        }
        
        return isFetchable;
    }
    
    
    @JsonIgnore
    public boolean isRootMembership()
    {
        return (origoId.substring(0, 1).equals("~"));
    }
    
    
    @JsonIgnore
    public boolean isResidency()
    {
        return type.equals("R");
    }
    
    
    @JsonIgnore
    public boolean isAssociate()
    {
        return type.equals("A");
    }
}
