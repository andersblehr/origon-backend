package com.origoapp.api.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfFalse;


@Subclass(unindexed = true)
@Unindexed
@Cached(expirationSeconds = 600)
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(value = {"origoKey", "memberKey"}, ignoreUnknown = true)
public class OMemberResidency extends OMembership
{
    public @NotSaved(IfFalse.class) boolean presentOn01Jan = true;
    public @NotSaved(IfDefault.class) int daysAtATime = 0;
    public @NotSaved(IfDefault.class) int switchDay = 0;
    public @NotSaved(IfDefault.class) int switchFrequency = 0;
    
    public @NotSaved Map<String, String> residentRef;
    public @NotSaved Map<String, String> residenceRef;
    
    
    public OMemberResidency()
    {
        super();
    }
}
