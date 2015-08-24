package co.origon.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "memberKey"}, ignoreUnknown = true)
public class OMembership extends OReplicatedEntity
{
    public String type;
    public @IgnoreSave(IfFalse.class) boolean isAdmin = false;
    public @IgnoreSave(IfNull.class) String status;
    public @IgnoreSave(IfNull.class) String affiliations;
    
    public @Ignore OMember member;
    public @Ignore Map<String, String> memberRef;
    public Key<OMember> memberKey;
    
    public @IgnoreSave Map<String, String> origoRef;
    

    public OMembership()
    {
        super();
    }
    
    
    @JsonIgnore
    public boolean isAssociate()
    {
        return type.equals("A");
    }
    
    
    @JsonIgnore
    public boolean isFetchable()
    {
        boolean isFetchable = false;
        
        if (!isExpired && !type.equals("F")) {
            isFetchable = type.equals("~") || type.equals("A") || (status != null && !status.equals("-"));
        }
        
        return isFetchable;
    }
    
    
    @JsonIgnore
    public boolean isInvitable()
    {
        boolean isInvitable = dateReplicated == null;
        isInvitable = isInvitable && member.hasEmail();
        isInvitable = isInvitable && type != null & (type.equals("P")|| type.equals("R") || type.equals("L") || type.equals("A"));
        isInvitable = isInvitable && ((status == null && type.equals("A")) || (status != null && (!status.equals("A") || type.equals("R"))));
        
        return isInvitable;
    }
}
