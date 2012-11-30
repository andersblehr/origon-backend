package com.origoapp.api.aux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import com.origoapp.api.auth.OAuthInfo;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.model.OReplicatedEntity;
import com.origoapp.api.model.OReplicatedEntityGhost;
import com.origoapp.api.model.ODevice;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMemberResidency;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OMessageBoard;
import com.origoapp.api.model.OOrigo;
import com.origoapp.api.model.OLinkedEntityRef;


public class ODAO extends DAOBase
{
    public OMeta m;

    
    static
    {
        ObjectifyService.register(OAuthInfo.class);
        ObjectifyService.register(OAuthMeta.class);
        
        ObjectifyService.register(OReplicatedEntityGhost.class);
        ObjectifyService.register(ODevice.class);
        ObjectifyService.register(OMessageBoard.class);
        ObjectifyService.register(OMember.class);
        ObjectifyService.register(OMemberResidency.class);
        ObjectifyService.register(OMembership.class);
        ObjectifyService.register(OOrigo.class);
        ObjectifyService.register(OLinkedEntityRef.class);
        
        ObjectifyService.register(OMemberProxy.class);
    }
    
    
    public ODAO(OMeta meta)
    {
        super();
        
        m = meta;
    }
    
    
    public <T> T get(Key<T> key)
    {
        try {
            return ofy().get(key);
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    
    public void putAuthToken(String authToken)
    {
        OMemberProxy memberProxy = m.getMemberProxy();
        Collection<OAuthMeta> authMetaItems = ofy().get(memberProxy.authMetaKeys).values();
        
        if (authMetaItems.size() > 0) {
            for (OAuthMeta authMeta : authMetaItems) {
                if (authMeta.deviceId.equals(m.getDeviceId())) {
                    memberProxy.authMetaKeys.remove(new Key<OAuthMeta>(OAuthMeta.class, authMeta.authToken));
                    ofy().delete(authMeta);
                    
                    OLog.log().fine(m.meta() + String.format("Deleted old auth token (token: %s; user: %s).", authMeta.authToken, m.getEmail()));
                }
            }
        } else {
            memberProxy.didRegister = true;
        }
        
        OAuthMeta authMeta = new OAuthMeta(authToken, m.getEmail(), m.getDeviceId(), m.getDeviceType());
        memberProxy.authMetaKeys.add(new Key<OAuthMeta>(OAuthMeta.class, authToken));
        
        ofy().put(authMeta, memberProxy);
        
        OLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getEmail()));
    }
    
    
    public void replicateEntities(List<OReplicatedEntity> entityList)
    {
        Set<OReplicatedEntity> entitiesToReplicate = new HashSet<OReplicatedEntity>();
        
        Map<String, Set<Key<OMembership>>> membershipKeysToAddByEmail = new HashMap<String, Set<Key<OMembership>>>();
        Map<String, Set<Key<OMembership>>> membershipKeysToDeleteByEmail = new HashMap<String, Set<Key<OMembership>>>();
        
        Set<String> emailAddressesOfAddedMembers = new HashSet<String>();
        Set<OMemberProxy> affectedMemberProxies = new HashSet<OMemberProxy>();
        Set<Key<OReplicatedEntity>> keysForEntitiesToDelete = new HashSet<Key<OReplicatedEntity>>();
        
        Date dateReplicated = new Date();
        
        for (OReplicatedEntity entity : entityList) {
            entity.origoKey = new Key<OOrigo>(OOrigo.class, entity.origoId);
            
            if (entity.getClass().equals(OReplicatedEntityGhost.class)) {
                OReplicatedEntityGhost entityGhost = (OReplicatedEntityGhost)entity;
                
                if (entityGhost.hasExpired) {
                    keysForEntitiesToDelete.add(new Key<OReplicatedEntity>(entityGhost.origoKey, OReplicatedEntity.class, entityGhost.entityId)); // TODO: Decide whether to support expiration
                } else {
                    entitiesToReplicate.add(entityGhost);
                    
                    if (entityGhost.ghostedEntityClass.equals(OMembership.class.getSimpleName()) || entityGhost.ghostedEntityClass.equals(OMemberResidency.class.getSimpleName())) {
                        String linkedEntityId = String.format("%s#%s", entityGhost.ghostedMembershipMemberId, entityGhost.origoKey.getRaw().getName());
                        keysForEntitiesToDelete.add(new Key<OReplicatedEntity>(entityGhost.origoKey, OReplicatedEntity.class, linkedEntityId));
                        
                        if (entityGhost.ghostedMembershipMemberEmail != null) {
                            Set<Key<OMembership>> membershipKeysToDeleteForEmail = membershipKeysToDeleteByEmail.get(entityGhost.ghostedMembershipMemberEmail);
                            
                            if (membershipKeysToDeleteForEmail == null) {
                                membershipKeysToDeleteForEmail = new HashSet<Key<OMembership>>();
                                membershipKeysToDeleteByEmail.put(entityGhost.ghostedMembershipMemberEmail, membershipKeysToDeleteForEmail);
                            }
                            
                            membershipKeysToDeleteForEmail.add(new Key<OMembership>(entityGhost.origoKey, OMembership.class, entityGhost.entityId));
                        }
                    }
                }
            } else {
                entitiesToReplicate.add(entity);
                
                if (entity.getClass().equals(OMember.class)) {
                    OMember member = (OMember)entity;
                    
                    if ((member.email != null) && (entity.dateReplicated == null)) {
                        emailAddressesOfAddedMembers.add(member.email);
                        
                        if (member.email.equals(m.getEmail())) {
                            OMemberProxy memberProxy = m.getMemberProxy();
                            memberProxy.memberId = member.entityId;
                            memberProxy.didRegister = true;
                            
                            affectedMemberProxies.add(memberProxy);
                        } else {
                            affectedMemberProxies.add(new OMemberProxy(member.email, member.entityId));
                        }
                    }
                } else if (entity.getClass().equals(OMembership.class) || entity.getClass().equals(OMemberResidency.class)) {
                    OMember member = ((OMembership)entity).member;
                    
                    if ((member.email != null) && (member.email.equals(m.getEmail()) || (member.dateReplicated == null))) {
                        Set<Key<OMembership>> membershipKeysToAddForEmail = membershipKeysToAddByEmail.get(member.email);
                        
                        if (membershipKeysToAddForEmail == null) {
                            membershipKeysToAddForEmail = new HashSet<Key<OMembership>>();
                            membershipKeysToAddByEmail.put(member.email, membershipKeysToAddForEmail);
                        }
                        
                        membershipKeysToAddForEmail.add(new Key<OMembership>(member.origoKey, OMembership.class, member.entityId));
                    }
                }
            }
            
            entity.dateReplicated = dateReplicated;
        }
        
        Set<Key<OMemberProxy>> missingMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
        
        for (String email : membershipKeysToAddByEmail.keySet()) {
            if (!emailAddressesOfAddedMembers.contains(email)) {
                missingMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, email));
            }
        }
        
        for (String email : membershipKeysToDeleteByEmail.keySet()) {
            missingMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, email));
        }

        if (missingMemberProxyKeys.size() > 0) {
            affectedMemberProxies.addAll(ofy().get(missingMemberProxyKeys).values());
        }

        for (OMemberProxy memberProxy : affectedMemberProxies) {
            Set<Key<OMembership>> addedMembershipKeysForMember = membershipKeysToAddByEmail.get(memberProxy.email);
            
            if (addedMembershipKeysForMember != null) {
                memberProxy.membershipKeys.addAll(membershipKeysToAddByEmail.get(memberProxy.email));
            }
            
            Set<Key<OMembership>> deletedMembershipKeysForMember = membershipKeysToDeleteByEmail.get(memberProxy.email);
            
            if (deletedMembershipKeysForMember != null) {
                memberProxy.membershipKeys.removeAll(deletedMembershipKeysForMember);
            }
        }
        
        if (affectedMemberProxies.size() > 0) {
            ofy().put(affectedMemberProxies);
        }
        
        ofy().put(entitiesToReplicate);
        OLog.log().fine(m.meta() + "Replicated entities: " + entitiesToReplicate.toString());

        if (keysForEntitiesToDelete.size() > 0) {
            ofy().delete(keysForEntitiesToDelete);
            OLog.log().fine(m.meta() + "Permanently deleted entities: " + keysForEntitiesToDelete);
        }
    }
    
    
    public List<OReplicatedEntity> fetchEntities(Date deviceReplicationDate)
    {
        OLog.log().fine(m.meta() + "Fetching entities modified since: " + ((deviceReplicationDate != null) ? deviceReplicationDate.toString() : "<dawn of time>"));
        
        Collection<OMembership> memberships = ofy().get(m.getMemberProxy().membershipKeys).values();
        
        Set<OReplicatedEntity> fetchedEntities = new HashSet<OReplicatedEntity>();
        Set<Key<OReplicatedEntity>> additionalEntityKeys = new HashSet<Key<OReplicatedEntity>>();
        
        for (OMembership membership : memberships) {
            if (membership.isActive || membership.entityId.startsWith("~") || (membership.getClass().equals(OMemberResidency.class))) {
                Iterable<OReplicatedEntity> membershipEntities = null;
                
                if (deviceReplicationDate != null) {
                    membershipEntities = ofy().query(OReplicatedEntity.class).ancestor(membership.origoKey).filter("dateReplicated >", deviceReplicationDate);
                } else {
                    membershipEntities = ofy().query(OReplicatedEntity.class).ancestor(membership.origoKey);
                }
                
                for (OReplicatedEntity entity : membershipEntities) {
                    if (entity.getClass().equals(OLinkedEntityRef.class)) {
                        additionalEntityKeys.add(((OLinkedEntityRef)entity).linkedEntityKey);
                    }
                    
                    fetchedEntities.add(entity);
                }
            } else {
                fetchedEntities.add(membership);
                additionalEntityKeys.add(new Key<OReplicatedEntity>(membership.origoKey, OReplicatedEntity.class, membership.origoId));
            }
        }
        
        fetchedEntities.addAll(ofy().get(additionalEntityKeys).values());
        
        OLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
        return new ArrayList<OReplicatedEntity>(fetchedEntities);
    }
}
