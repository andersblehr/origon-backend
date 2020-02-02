package co.origon.api.model.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.util.Date;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@With
@Builder
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class DeviceCredentials {

  private final String deviceToken;
  private final String userEmail;
  private final String deviceId;
  private final String deviceType;

  @Builder.Default
  private final Date expiresAt = new Date(System.currentTimeMillis() + 30 * 86400 * 1000L);
}
