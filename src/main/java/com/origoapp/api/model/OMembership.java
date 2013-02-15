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
@JsonIgnoreProperties(value = {"origoKey", "memberKey", "associateMemberKey"}, ignoreUnknown = true)
public class OMembership extends OReplicatedEntity
{
    public @IgnoreSave(IfFalse.class) boolean isActive = false;
    public @IgnoreSave(IfFalse.class) boolean isAdmin = false;
    
    public @IgnoreSave(IfNull.class) String contactRole;
    public @IgnoreSave(IfNull.class) String contactType;
    
    public @IgnoreSave OMember member;
    public @IgnoreSave Map<String, String> memberRef;
    public @IgnoreSave(IfNull.class) Key<OMember> memberKey;
    
    public @IgnoreSave Map<String, String> origoRef;
    
    public @IgnoreSave OMember associateMember;
    public @IgnoreSave Map<String, String> associateMemberRef;
    public @IgnoreSave(IfNull.class) Key<OMember> associateMemberKey;
    
    public @IgnoreSave Map<String, String> associateOrigoRef;
    

    public OMembership()
    {
        super();
    }
    
    
    public boolean isRootMembership()
    {
        return (entityId.substring(0, 1).equals("~"));
    }
    
    
    public boolean isResidency()
    {
        return this.getClass().equals(OMemberResidency.class);
    }
    
    
    public boolean isAssociate()
    {
        return ((associateMember != null) || (associateMemberKey != null));
    }
    
    
    public OMember getMember()
    {
        return isAssociate() ? associateMember : member;
    }
}
