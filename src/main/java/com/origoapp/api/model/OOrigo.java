package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.condition.IfNull;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "parentOrigoKey"}, ignoreUnknown = true)
public class OOrigo extends OReplicatedEntity
{
    public @IgnoreSave(IfNull.class) String name;
    public String type;
    
    public @IgnoreSave(IfNull.class) String descriptionText;
    public @IgnoreSave(IfNull.class) String address;
    public @IgnoreSave(IfNull.class) String telephone;
    public @IgnoreSave(IfNull.class) Blob photo;
    
    public @IgnoreSave OOrigo parentOrigo;
    public @IgnoreSave @EmbedMap Map<String, String> parentOrigoRef;
    public @IgnoreSave(IfNull.class) Key<OOrigo> parentOrigoKey;


    public OOrigo()
    {
        super();
    }
}
