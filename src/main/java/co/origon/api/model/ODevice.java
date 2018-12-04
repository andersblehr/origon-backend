package co.origon.api.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Subclass;


@Subclass
@Cache(expirationSeconds = 600)
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "userKey"}, ignoreUnknown = true)
public class ODevice extends OReplicatedEntity
{
    public String type;
    public String name;
    public Date lastSeen;
    
    public @Ignore OMember user;
    public @Ignore Map<String, String> userRef;
    public Key<OMember> userKey;

    
    public ODevice()
    {
        super();
    }
}
