package co.origon.api.model.server.ofy;

import co.origon.api.model.server.OneTimeCredentials;
import co.origon.api.repository.ofy.OfyMapper;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class OAuthInfo implements OfyMapper<OneTimeCredentials> {
  @Id private String email;
  private String deviceId;
  private String passwordHash;
  private String activationCode;

  public OAuthInfo(OneTimeCredentials oneTimeCredentials) {
    email = oneTimeCredentials.email();
    deviceId = oneTimeCredentials.deviceId();
    passwordHash = oneTimeCredentials.passwordHash();
    activationCode = oneTimeCredentials.activationCode();
  }

  @Override
  public OneTimeCredentials fromOfy() {
    return OneTimeCredentials.builder()
        .email(email)
        .deviceId(deviceId)
        .passwordHash(passwordHash)
        .activationCode(activationCode)
        .build();
  }
}
