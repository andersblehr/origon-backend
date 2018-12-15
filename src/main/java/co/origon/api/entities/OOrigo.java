package co.origon.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNotNull;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Cache(expirationSeconds = 600)
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "parentOrigoKey"}, ignoreUnknown = true)
public class OOrigo extends OReplicatedEntity
{
    public @IgnoreSave(IfNull.class) String name;
    public String type;
    
    public @IgnoreSave(IfNull.class) String descriptionText;
    public @IgnoreSave(IfNull.class) String address;
    public @IgnoreSave(IfNull.class) String location;
    public @IgnoreSave(IfNull.class) String telephone;
    public @IgnoreSave(IfNull.class) String permissions;
    public @IgnoreSave(IfFalse.class) boolean isForMinors;
    
    public @IgnoreSave(IfNull.class) String joinCode;
    public @Index(IfNotNull.class) @IgnoreSave(IfNull.class) String internalJoinCode;

    
    public OOrigo()
    {
        super();
    }
    
    
    public boolean takesPrecedenceOver(OOrigo origo)
    {
        boolean takesPrecedence = false;
        
        if (origo == null) {
            takesPrecedence = true;
        } else if (type != null && !type.equals("~")) {
            if (origo.isPrivate() || !origo.isResidence()) {
                takesPrecedence = !isPrivate(); 
            }
        }
        
        return takesPrecedence;
    }
    
    
    @JsonIgnore
    public boolean isResidence()
    {
        return type != null && type.equals("residence");
    }
    
    
    @JsonIgnore
    public boolean isPrivate()
    {
        return type != null && type.equals("private");
    }
}
