package co.origon.api.model.api.entity;

import co.origon.api.model.api.Entity;

import java.util.Collection;

public interface MemberProxy extends Entity<MemberProxy> {

  MemberProxy proxyId(String proxyId);

  MemberProxy memberId(String memberId);

  MemberProxy memberName(String memberName);

  MemberProxy passwordHash(String passwordHash);

  MemberProxy deviceToken(String deviceToken);

  MemberProxy deviceTokens(Collection<String> deviceTokens);

  MemberProxy membershipIds(Collection<String> membershipIds);

  String proxyId();

  String memberId();

  String memberName();

  String passwordHash();

  Collection<String> deviceTokens();

  Collection<String> membershipIds();

  boolean isRegistered();

  void refreshDeviceToken(String deviceToken, String deviceId);
}
