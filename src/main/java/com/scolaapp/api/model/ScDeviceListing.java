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


@Subclass
@Unindexed
@Cached(expirationSeconds=600)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(value={"scolaKey", "deviceKey", "memberKey"}, ignoreUnknown=true)
public class ScDeviceListing extends ScCachedEntity
{
    public String displayName;
    
    public @NotSaved ScDevice device;
    public @NotSaved Map<String, String> deviceRef;
    public Key<ScDevice> deviceKey;
    
    public @NotSaved ScScolaMember member;
    public @NotSaved Map<String, String> memberRef;
    public Key<ScScolaMember> memberKey;
    
    
    public ScDeviceListing() {}
}
