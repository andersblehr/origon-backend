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
	public String passwordHash;
	public String workPhone;

    
    public ScPerson()
    {
    }
}
