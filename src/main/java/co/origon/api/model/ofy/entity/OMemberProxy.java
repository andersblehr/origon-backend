package co.origon.api.model.ofy.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import co.origon.api.model.api.entity.MemberProxy;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.condition.IfEmpty;
import com.googlecode.objectify.condition.IfNull;

import lombok.*;
import lombok.experimental.Accessors;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Entity
@Cache(expirationSeconds = 600)
@Data
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class OMemberProxy implements MemberProxy {
  @Id private String proxyId;
  private String memberId;

  @IgnoreSave(IfNull.class)
  private String memberName;

  @IgnoreSave(IfNull.class)
  private String passwordHash;

  @IgnoreSave(IfEmpty.class)
  @Singular
  private Set<Key<OAuthMeta>> authMetaKeys;

  @IgnoreSave(IfEmpty.class)
  @Singular
  private Set<Key<OMembership>> membershipKeys;

  public OMemberProxy() {
    this.authMetaKeys = new HashSet<>();
    this.membershipKeys = new HashSet<>();
  }

  public OMemberProxy(String proxyId) {
    this();

    this.proxyId = proxyId;
  }

  public OMemberProxy(OMember member) {
    this(member.getProxyId());

    this.memberId = member.entityId;
  }

  public OMemberProxy(String proxyId, OMemberProxy instanceToClone) {
    this(proxyId);

    memberId = instanceToClone.memberId;
    passwordHash = instanceToClone.passwordHash;
    authMetaKeys = instanceToClone.authMetaKeys;
    membershipKeys = instanceToClone.membershipKeys;
  }

  public static OMemberProxy get(String email) {
    OMemberProxy memberProxy = ofy().load().type(OMemberProxy.class).id(email).now();
    return memberProxy != null ? memberProxy : new OMemberProxy(email);
  }

  @Override
  public MemberProxy key(String key) {
    proxyId = key;
    return this;
  }

  @Override
  public MemberProxy deviceToken(String deviceToken) {
    authMetaKeys.add(Key.create(OAuthMeta.class, deviceToken));
    return this;
  }

  @Override
  public MemberProxy deviceTokens(Collection<String> deviceTokens) {
    authMetaKeys =
        deviceTokens.stream()
            .map(deviceToken -> Key.create(OAuthMeta.class, deviceToken))
            .collect(Collectors.toSet());
    return this;
  }

  @Override
  public MemberProxy membershipIds(Collection<String> membershipIds) {
    membershipKeys =
        membershipIds.stream()
            .map(membershipId -> Key.create(OMembership.class, membershipId))
            .collect(Collectors.toSet());
    return this;
  }

  @Override
  public Collection<String> deviceTokens() {
    return authMetaKeys.stream()
        .map(authMetaKey -> authMetaKey.getRaw().getName())
        .collect(Collectors.toSet());
  }

  @Override
  public Collection<String> membershipIds() {
    return membershipKeys.stream()
        .map(membershipKey -> membershipKey.getRaw().getName())
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isRegistered() {
    return passwordHash != null;
  }

  @Override
  public void refreshDeviceToken(String deviceToken, String deviceId) {
    final Collection<Key<OAuthMeta>> redundantAuthMetaKeys =
        ofy().load().keys(authMetaKeys).values().stream()
            .filter(authMeta -> authMeta.deviceId().equals(deviceId))
            .map(authMeta -> Key.create(OAuthMeta.class, authMeta.authToken()))
            .collect(Collectors.toSet());

    authMetaKeys.add(Key.create(OAuthMeta.class, deviceToken));
    authMetaKeys =
        authMetaKeys.stream()
            .filter(key -> !redundantAuthMetaKeys.contains(key))
            .collect(Collectors.toSet());

    ofy().delete().keys(redundantAuthMetaKeys).now();
  }

  public Set<Key<OAuthMeta>> getAuthMetaKeys() {
    return authMetaKeys;
  }

  public Set<Key<OMembership>> getMembershipKeys() {
    return membershipKeys;
  }

  @OnLoad
  public void instantiateNullSets() {
    if (authMetaKeys == null) {
      authMetaKeys = new HashSet<>();
    }

    if (membershipKeys == null) {
      membershipKeys = new HashSet<>();
    }
  }
}
