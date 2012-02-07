package com.scolaapp.api.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.*;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScPerson")
public class ScPerson extends ScCachedEntity
{
	public Date birthday;
    public @Id String email;
    public String gender;
    public Boolean isActive;
    public Boolean isMinor;
    public String mobilePhone;
	public String name;
	
	public Key<ScHousehold> householdKey;
	public Key<ScHousehold> partTimeHouseholdKey;
    public @NotSaved ScHousehold household;
    public @NotSaved ScHousehold partTimeHousehold;

	
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
	    
	    birthday = clone.birthday;
	    gender = clone.gender;
	    isActive = clone.isActive;
	    isMinor = clone.isMinor;
	    mobilePhone = clone.mobilePhone;
	}
}
