package co.origon.api.model.ofy.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import java.util.Date;

@Entity
public class OAuthMeta {

  @Id private String authToken;
  private String email;
  private String deviceId;
  private String deviceType;
  private Date dateExpires = dateExpires();

  public String deviceToken() {
    return authToken;
  }

  public Date dateExpires() {
    if (dateExpires == null)
      dateExpires = new Date(System.currentTimeMillis() + 30 * 86400 * 1000L);
    return dateExpires;
  }
}
