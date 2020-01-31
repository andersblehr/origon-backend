package co.origon.api.model.ofy.entity;

import co.origon.api.model.EntityKey;
import co.origon.api.model.api.entity.MemberProxy;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.condition.IfEmpty;
import com.googlecode.objectify.condition.IfNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

@Entity
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
  private Set<EntityKey> membershipKeys;

  public OMemberProxy() {
    this.authMetaKeys = new HashSet<>();
    this.membershipKeys = new HashSet<>();
  }

  public OMemberProxy(String proxyId) {
    this();

    this.proxyId = proxyId;
  }

  public OMemberProxy(OMember member) {
    this(member.proxyId());

    this.memberId = member.entityId;
  }

  public OMemberProxy(String proxyId, OMemberProxy instanceToClone) {
    this(proxyId);

    memberId = instanceToClone.memberId;
    passwordHash = instanceToClone.passwordHash;
    authMetaKeys = instanceToClone.authMetaKeys;
    membershipKeys = instanceToClone.membershipKeys;
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
  public MemberProxy membershipKeys(Collection<EntityKey> membershipKeys) {
    return null;
  }

  @Override
  public Collection<String> deviceTokens() {
    return authMetaKeys.stream()
        .map(authMetaKey -> authMetaKey.getRaw().getName())
        .collect(Collectors.toSet());
  }

  @Override
  public String id() {
    return proxyId;
  }

  @Override
  public boolean isRegistered() {
    return passwordHash != null;
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
