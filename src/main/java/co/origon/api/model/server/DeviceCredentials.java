package co.origon.api.model.server;

import java.util.Date;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@With
@Builder
@Accessors(fluent = true)
public class DeviceCredentials {

  private final String deviceToken;
  private final String userEmail;
  private final String deviceId;
  private final String deviceType;

  @Builder.Default
  private final Date expiresAt = new Date(System.currentTimeMillis() + 30 * 86400 * 1000L);
}
