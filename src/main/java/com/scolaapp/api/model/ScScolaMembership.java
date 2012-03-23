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


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey", "memberKey"}, ignoreUnknown=true)
public class ScScolaMembership extends ScCachedEntity
{
    public @NotSaved(IfDefault.class) boolean isActive = false;
    public @NotSaved(IfDefault.class) boolean isAdmin = false;
    public @NotSaved(IfDefault.class) boolean isCoach = false;
    public @NotSaved(IfDefault.class) boolean isTeacher = false;
    
    public @NotSaved ScScolaMember member;
    public @NotSaved Map<String, String> memberRef;
    public @Indexed Key<ScScolaMember> memberKey;

    
    public ScScolaMembership() {}
}
