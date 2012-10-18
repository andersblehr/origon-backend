package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfNull;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "parentOrigoKey"}, ignoreUnknown = true)
public class OOrigo extends OCachedEntity
{
    public String name;
    public @NotSaved(IfNull.class) String descriptionText;
    
    public @NotSaved(IfNull.class) String addressLine1;
    public @NotSaved(IfNull.class) String addressLine2;
    public @NotSaved(IfNull.class) String telephone;
    public @NotSaved(IfNull.class) Blob photo;
    
    public @NotSaved OOrigo parentOrigo;
    public @NotSaved Map<String, String> parentOrigoRef;
    public @NotSaved(IfNull.class) Key<OOrigo> parentOrigoKey;


    public OOrigo()
    {
        super();
    }
}
