package com.origoapp.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey"}, ignoreUnknown = true)
public class OReplicatedEntityGhost extends OReplicatedEntity
{
    public @IgnoreSave String ghostedEntityClass;
    public @IgnoreSave String ghostedMembershipMemberId;
    public @IgnoreSave String ghostedMembershipMemberEmail;
    public @IgnoreSave boolean hasExpired = false;
    
    
    public OReplicatedEntityGhost()
    {
        super();
    }
}
