package com.origoapp.api.aux;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.PostLoad;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfEmpty;
import com.googlecode.objectify.condition.IfNull;

import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.model.OMembership;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class OMemberProxy
{
    public @Id String email;
    
    public @NotSaved(IfDefault.class) boolean didRegister = false;
    public @NotSaved(IfNull.class) String passwordHash;
    
    public @NotSaved(IfEmpty.class) Set<Key<OAuthMeta>> authMetaKeys;
    public @NotSaved(IfEmpty.class) Set<Key<OMembership>> membershipKeys;
    
    
    public OMemberProxy() {}
    
    
    public OMemberProxy(String email)
    {
        this.email = email;
        
        authMetaKeys = new HashSet<Key<OAuthMeta>>();
        membershipKeys = new HashSet<Key<OMembership>>();
    }
    
    
    @PostLoad
    public void instantiateNullSets()
    {
        if (authMetaKeys == null) {
            authMetaKeys = new HashSet<Key<OAuthMeta>>();
        }
        
        if (membershipKeys == null) {
            membershipKeys = new HashSet<Key<OMembership>>();
        }
    }
}
