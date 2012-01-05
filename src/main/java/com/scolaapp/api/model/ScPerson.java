package com.scolaapp.api.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.*;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;


@Unindexed
@Cached(expirationSeconds=600)
@XmlRootElement(name="ScPerson")
public class ScPerson
{
	public Date birthday;
    public @Id String email;
    public String gender;
    public Boolean isActive;
    public Boolean isMinor;
    public String mobilePhone;
	public String name;
	public String workPhone;

    
	public ScPerson(String email_, String name_)
	{
	    email = email_;
	    name = name_;
	}
	
	
	public ScPerson(ScPerson clone)
	{
	    birthday = clone.birthday;
	    email = clone.email;
	    gender = clone.gender;
	    isActive = clone.isActive;
	    isMinor = clone.isMinor;
	    mobilePhone = clone.mobilePhone;
	    name = clone.name;
	    workPhone = clone.workPhone;
	}
	
	
    public ScPerson() {}
}
