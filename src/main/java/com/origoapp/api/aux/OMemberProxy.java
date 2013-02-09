package com.origoapp.api.aux;

import java.util.Collection;
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
import com.origoapp.api.model.OMembership;


@Entity
@Cache(expirationSeconds=600)
public class OMemberProxy
{
    public @Id String proxyId;
    
    public @IgnoreSave(IfDefault.class) boolean didSignUp = false;
    public @IgnoreSave(IfNull.class) String passwordHash;
    
    private @IgnoreSave(IfEmpty.class) Set<Key<OAuthMeta>> authMetaKeys;
    private @IgnoreSave(IfEmpty.class) Set<Key<OMembership>> membershipKeys;
    
    private @IgnoreSave Set<String> membershipOrigoIds;
    private @IgnoreSave(IfEmpty.class) Set<String> associatedOrigoIds;
    
    
    public OMemberProxy() {}
    
    
    public OMemberProxy(String proxyId)
    {
        this.proxyId = proxyId;
        
        authMetaKeys = new HashSet<Key<OAuthMeta>>();
        membershipKeys = new HashSet<Key<OMembership>>();
        membershipOrigoIds = new HashSet<String>();
        associatedOrigoIds = new HashSet<String>();
    }

    
    public OMemberProxy(String proxyId, OMemberProxy instanceToClone)
    {
        this.proxyId = proxyId;
        
        didSignUp = instanceToClone.didSignUp;
        passwordHash = instanceToClone.passwordHash;
        
        authMetaKeys = instanceToClone.authMetaKeys;
        membershipKeys = instanceToClone.membershipKeys;
        
        membershipOrigoIds = instanceToClone.membershipOrigoIds;
        associatedOrigoIds = instanceToClone.associatedOrigoIds;
    }
    
    
    public Set<Key<OAuthMeta>> getAuthMetaKeys()
    {
        return authMetaKeys;
    }
    
    
    public void addAuthMetaKey(Key<OAuthMeta> authMetaKey)
    {
        authMetaKeys.add(authMetaKey);
    }
    
    
    public void removeAuthMetaKey(Key<OAuthMeta> authMetaKey)
    {
        authMetaKeys.remove(authMetaKey);
    }
    
    
    public Set<Key<OMembership>> getMembershipKeys()
    {
        return membershipKeys;
    }
    
    
    public void addMembershipKeys(Collection<Key<OMembership>> membershipKeys)
    {
        this.membershipKeys.addAll(membershipKeys);
        
        for (Key<OMembership> membershipKey : membershipKeys) {
            membershipOrigoIds.add(membershipKey.getParent().getRaw().getName());
        }
    }
    
    
    public void removeMembershipKey(Key<OMembership> membershipKey)
    {
        membershipKeys.remove(membershipKey);
        membershipOrigoIds.remove(membershipKey.getParent().getRaw().getName());
    }
    
    
    public void removeMembershipKeys(Collection<Key<OMembership>> membershipKeys)
    {
        this.membershipKeys.removeAll(membershipKeys);
        
        for (Key<OMembership> membershipKey : membershipKeys) {
            membershipOrigoIds.remove(membershipKey.getParent().getRaw().getName());
        }
    }
    
    
    public Set<String> getAssociatedOrigoIds()
    {
        return associatedOrigoIds;
    }
    
    
    public void addAssociatedOrigoId(String origoId)
    {
        associatedOrigoIds.add(origoId);
    }
    
    
    public boolean isMemberOfOrigoWithId(String origoId)
    {
        return membershipOrigoIds.contains(origoId);
    }
    
    
    public boolean isAssociatedWithOrigoWithId(String origoId)
    {
        return associatedOrigoIds.contains(origoId);
    }
    
    
    @OnLoad
    public void instantiateNullSets()
    {
        if (authMetaKeys == null) {
            authMetaKeys = new HashSet<Key<OAuthMeta>>();
        }
        
        membershipOrigoIds = new HashSet<String>();
        
        if (membershipKeys == null) {
            membershipKeys = new HashSet<Key<OMembership>>();
        } else {
            for (Key<OMembership> membershipKey : membershipKeys) {
                membershipOrigoIds.add(membershipKey.getParent().getRaw().getName());
            }
        }
        
        if (associatedOrigoIds == null) {
            associatedOrigoIds = new HashSet<String>();
        }
    }
}
