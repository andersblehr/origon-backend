package co.origon.api.model;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Subclass;


@Subclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
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
