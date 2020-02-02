package co.origon.api.model.server.ofy;

import co.origon.api.model.EntityKey;
import co.origon.api.model.client.ofy.OMembership;
import co.origon.api.model.client.ofy.OOrigo;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.repository.ofy.OfyMapper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class OMemberProxy implements OfyMapper<MemberProxy> {

  @Id private String proxyId;
  private String memberId;
  private String memberName;
  private String passwordHash;
  private Set<Key<OAuthMeta>> authMetaKeys;
  private Set<Key<OMembership>> membershipKeys;

  @OnLoad
  public void instantiateNullSets() {
    if (authMetaKeys == null) {
      authMetaKeys = new HashSet<>();
    }
    if (membershipKeys == null) {
      membershipKeys = new HashSet<>();
    }
  }

  public static OMemberProxy toOfy(MemberProxy modelMemberProxy) {
    return new OMemberProxy(modelMemberProxy);
  }

  @Override
  public MemberProxy fromOfy() {
    return MemberProxy.builder()
        .id(proxyId)
        .memberId(memberId)
        .memberName(memberName)
        .passwordHash(passwordHash)
        .deviceTokens(
            authMetaKeys.stream()
                .map(authMetaKey -> authMetaKey.getRaw().getName())
                .collect(Collectors.toSet()))
        .membershipKeys(
            membershipKeys.stream()
                .map(
                    ofyKey ->
                        EntityKey.from(
                            ofyKey.getRaw().getName(), ofyKey.getParent().getRaw().getName()))
                .collect(Collectors.toSet()))
        .build();
  }

  private OMemberProxy(MemberProxy memberProxy) {
    this.proxyId = memberProxy.id();
    this.memberId = memberProxy.memberId();
    this.memberName = memberProxy.memberName();
    this.passwordHash = memberProxy.passwordHash();
    this.authMetaKeys =
        memberProxy.deviceTokens().stream()
            .map(token -> Key.create(OAuthMeta.class, token))
            .collect(Collectors.toSet());
    this.membershipKeys =
        memberProxy.membershipKeys().stream()
            .map(
                entityKey ->
                    Key.create(
                        Key.create(OOrigo.class, entityKey.parentId()),
                        OMembership.class,
                        entityKey.entityId()))
            .collect(Collectors.toSet());
  }
}
