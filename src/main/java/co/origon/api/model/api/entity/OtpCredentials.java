package co.origon.api.model.api.entity;

import co.origon.api.model.api.Entity;

public interface OtpCredentials extends Entity<OtpCredentials> {

  OtpCredentials email(String email);

  OtpCredentials deviceId(String deviceId);

  OtpCredentials passwordHash(String passwordHash);

  OtpCredentials activationCode(String activationCode);

  String email();

  String deviceId();

  String passwordHash();

  String activationCode();
}
