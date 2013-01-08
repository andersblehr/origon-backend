package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfFalse;


@EntitySubclass
@Cache(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "memberKey"}, ignoreUnknown = true)
public class OMemberResidency extends OMembership
{
    public @IgnoreSave(IfFalse.class) boolean presentOn01Jan = true;
    public @IgnoreSave(IfDefault.class) int daysAtATime = 0;
    public @IgnoreSave(IfDefault.class) int switchDay = 0;
    public @IgnoreSave(IfDefault.class) int switchFrequency = 0;
    
    public @IgnoreSave Map<String, String> residentRef;
    public @IgnoreSave Map<String, String> residenceRef;
    
    
    public OMemberResidency()
    {
        super();
    }
}
