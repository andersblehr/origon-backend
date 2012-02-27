package com.scolaapp.api.model;

import java.util.List;

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
public class ScScola extends ScCachedEntity
{
    public String descriptionText;
    public String name;
    
    public Key<ScMessageBoard> adminMessageBoardKey;
    public Key<ScScola> guardedScolaKey;
    public Key<ScScola> guardianScolaKey;
    
    public @NotSaved ScMessageBoard adminMessageBoard;
    public @NotSaved ScScola guardedScola;
    public @NotSaved ScScola guardianScola;
    
    public @NotSaved List<ScScolaMember> admins;
    public @NotSaved List<ScPerson> coaches;
    public @NotSaved List<ScScolaMember> membersActive;
    public @NotSaved List<ScPerson> membersInactive;
    public @NotSaved List<ScMessageBoard> messageBoards;
    
    
    public ScScola()
    {
        super();
    }
}
