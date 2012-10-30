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
import com.origoapp.api.model.OReplicatedEntity;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMemberResidency;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OOrigo;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class OMemberProxy
{
    public @Id String userId;
    
    public @NotSaved(IfDefault.class) boolean didRegister = false;
    public @NotSaved(IfNull.class) String passwordHash;
    
    public @NotSaved(IfEmpty.class) Set<Key<OAuthMeta>> authMetaKeys;
    public @NotSaved(IfEmpty.class) Set<Key<OMembership>> membershipKeys;

    public @NotSaved Key<OReplicatedEntity> origoKey;
    public @NotSaved Key<OReplicatedEntity> memberKey;
    public @NotSaved Set<Key<OReplicatedEntity>> residencyKeys;
    public @NotSaved Set<Key<OReplicatedEntity>> residenceKeys;
    
    
    public OMemberProxy() {}
    
    
    public OMemberProxy(String userId)
    {
        this.userId = userId;
        
        origoKey = new Key<OReplicatedEntity>(OOrigo.class, userId);
        memberKey = new Key<OReplicatedEntity>(origoKey, OMember.class, userId);
        
        authMetaKeys = new HashSet<Key<OAuthMeta>>();
        membershipKeys = new HashSet<Key<OMembership>>();
    }
    
    
    @PostLoad
    public void populateNonSavedValues()
    {
        origoKey = new Key<OReplicatedEntity>(OOrigo.class, userId);
        memberKey = new Key<OReplicatedEntity>(origoKey, OMember.class, userId);
        
        if (authMetaKeys == null) {
            authMetaKeys = new HashSet<Key<OAuthMeta>>();
        }
        
        if (membershipKeys == null) {
            membershipKeys = new HashSet<Key<OMembership>>();
        }
        
        residencyKeys = new HashSet<Key<OReplicatedEntity>>();
        residenceKeys = new HashSet<Key<OReplicatedEntity>>();
        
        for (Key<OMembership> membershipKey : membershipKeys) {
            Key<OOrigo> origoKey = new Key<OOrigo>(OOrigo.class, membershipKey.getRaw().getParent().getName());
            String membershipId = membershipKey.getRaw().getName();
            
            if (membershipId.startsWith(userId)) {
                residencyKeys.add(new Key<OReplicatedEntity>(origoKey, OMemberResidency.class, membershipId));
                residenceKeys.add(new Key<OReplicatedEntity>(origoKey, OOrigo.class, membershipId.substring(membershipId.indexOf("$") + 1)));
            }
        }
    }
}
