package co.origon.api.model.ofy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class OAuthInfo {
  @Id private String email;
  private String deviceId;
  private String passwordHash;
  private String activationCode;
}
