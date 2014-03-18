package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "userKey"}, ignoreUnknown = true)
public class OSettings extends OReplicatedEntity
{
    public boolean useEnglish;
    
    public @IgnoreSave OMember user;
    public @IgnoreSave @EmbedMap Map<String, String> userRef;
    public Key<OMember> userKey;

    
    public OSettings()
    {
        super();
    }
}
