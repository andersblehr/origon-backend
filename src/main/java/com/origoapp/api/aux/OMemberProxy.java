package com.origoapp.api.aux;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.condition.IfDefault;
import com.googlecode.objectify.condition.IfEmpty;
import com.googlecode.objectify.condition.IfNull;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMembership;


@Entity
@Cache(expirationSeconds=600)
public class OMemberProxy
{
    public @Id String proxyId;
    public String memberId;
    
    public @IgnoreSave(IfDefault.class) boolean didSignUp = false;
    public @IgnoreSave(IfNull.class) String passwordHash;
    
    public @IgnoreSave(IfEmpty.class) Set<Key<OAuthMeta>> authMetaKeys;
    public @IgnoreSave(IfEmpty.class) Set<Key<OMembership>> membershipKeys;
    
    
    public OMemberProxy() {}
    
    
    public OMemberProxy(OMember member)
    {
        this(member.getProxyId());
        
        this.memberId = member.entityId;
    }
    
    
    public OMemberProxy(String proxyId)
    {
        this.proxyId = proxyId;
        
        authMetaKeys = new HashSet<Key<OAuthMeta>>();
        membershipKeys = new HashSet<Key<OMembership>>();
    }

    
    public OMemberProxy(String proxyId, OMemberProxy instanceToClone)
    {
        this.proxyId = proxyId;
        
        memberId = instanceToClone.memberId;
        didSignUp = instanceToClone.didSignUp;
        passwordHash = instanceToClone.passwordHash;
        authMetaKeys = instanceToClone.authMetaKeys;
        membershipKeys = instanceToClone.membershipKeys;
    }
        
    
    @OnLoad
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
