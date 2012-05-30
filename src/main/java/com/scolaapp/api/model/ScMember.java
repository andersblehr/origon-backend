package com.scolaapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"scolaKey"}, ignoreUnknown = true)
public class ScMember extends ScCachedEntity
{
    public String name;
    public String gender;
    public Date dateOfBirth;
    public String mobilePhone;
    
    public Date activeSince;
    public String passwordHash;
    
    public @NotSaved(IfFalse.class) boolean didRegister = false;
    
    
    public ScMember()
    {
        super();
    }
}
