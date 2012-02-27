package com.scolaapp.api.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScPerson extends ScCachedEntity
{
	public Date dateOfBirth;
    public String email;
    public String gender;
    public Boolean isActive;
    public Boolean isMinor;
    public String mobilePhone;
	public String name;
	
	public Key<ScHousehold> householdKey;
	
    public @NotSaved ScHousehold household;

	
	public ScPerson()
	{
	    super();
	}
	
    
	public ScPerson(String email_, String name_)
	{
	    super();
	    
	    email = email_;
	    name = name_;
	}
	
	
	public ScPerson(ScPerson clone)
	{
	    this(clone.email, clone.name);
	    
	    dateOfBirth = clone.dateOfBirth;
	    gender = clone.gender;
	    isActive = clone.isActive;
	    isMinor = clone.isMinor;
	    mobilePhone = clone.mobilePhone;
	}
}
