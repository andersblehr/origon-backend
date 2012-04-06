package com.scolaapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;


@Subclass(unindexed=true)
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"scolaKey", "memberKey"}, ignoreUnknown=true)
public class ScDevice extends ScCachedEntity
{
    public String type;
    public String displayName;
    
    public @NotSaved ScMember member;
    public @NotSaved Map<String, String> memberRef;
    public Key<ScMember> memberKey;

    
    public ScDevice()
    {
        super();
    }
}
