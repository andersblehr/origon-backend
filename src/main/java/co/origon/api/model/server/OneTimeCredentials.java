package co.origon.api.model.server;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@With
@Builder
@Accessors(fluent = true)
public class OneTimeCredentials {

  private final String email;
  private final String deviceId;
  private final String passwordHash;
  private final String activationCode;
}
