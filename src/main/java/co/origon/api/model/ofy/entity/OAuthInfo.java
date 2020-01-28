package co.origon.api.model.ofy.entity;

import co.origon.api.model.api.entity.OtpCredentials;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class OAuthInfo implements OtpCredentials {
  @Id private String email;
  private String deviceId;
  private String passwordHash;
  private String activationCode;
}
