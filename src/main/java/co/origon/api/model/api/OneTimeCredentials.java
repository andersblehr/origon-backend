package co.origon.api.model.api;

public interface OneTimeCredentials extends Entity<OneTimeCredentials> {

  OneTimeCredentials email(String email);

  OneTimeCredentials deviceId(String deviceId);

  OneTimeCredentials passwordHash(String passwordHash);

  OneTimeCredentials activationCode(String activationCode);

  String email();

  String deviceId();

  String passwordHash();

  String activationCode();
}
