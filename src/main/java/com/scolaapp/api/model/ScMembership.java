package com.scolaapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfNull;


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey", "memberKey", "partTimeResidencyKey"}, ignoreUnknown=true)
public class ScMembership extends ScCachedEntity
{
    public @NotSaved(IfDefault.class) boolean isResidency = true;
    
    public @NotSaved(IfDefault.class) boolean isActive = false;
    public @NotSaved(IfDefault.class) boolean isAdmin = false;
    public @NotSaved(IfDefault.class) boolean isCoach = false;
    public @NotSaved(IfDefault.class) boolean isTeacher = false;
    
    public @NotSaved Map<String, String> scolaRef;
    
    public @NotSaved ScMember member;
    public @NotSaved Map<String, String> memberRef;
    public @Indexed Key<ScMember> memberKey;

    public @NotSaved ScMemberResidency partTimeResidency;
    public @NotSaved Map<String, String> partTimeResidencyRef;
    public @NotSaved(IfNull.class) Key<ScMemberResidency> partTimeResidencyKey;

    
    public ScMembership()
    {
        super();
    }
}
