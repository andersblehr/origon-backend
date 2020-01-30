package co.origon.api.model.api.entity;

import co.origon.api.model.EntityKey;

import java.util.Collection;

public interface MemberProxy extends Entity<MemberProxy> {

  MemberProxy memberId(String memberId);

  MemberProxy memberName(String memberName);

  MemberProxy passwordHash(String passwordHash);

  MemberProxy deviceToken(String deviceToken);

  MemberProxy deviceTokens(Collection<String> deviceTokens);

  MemberProxy membershipKeys(Collection<EntityKey> membershipKeys);

  String memberId();

  String memberName();

  String passwordHash();

  Collection<String> deviceTokens();

  Collection<EntityKey> membershipKeys();

  boolean isRegistered();

  void refreshDeviceToken(String deviceToken, String deviceId);
}
