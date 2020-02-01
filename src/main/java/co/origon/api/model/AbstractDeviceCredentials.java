package co.origon.api.model;

import co.origon.api.annotation.StyledImmutable;
import java.util.Date;
import org.immutables.value.Value;

@Value.Immutable
@StyledImmutable
public abstract class AbstractDeviceCredentials {

  public abstract String email();

  public abstract String deviceToken();

  public abstract String deviceId();

  public abstract String deviceType();

  @Value.Default
  public Date expiresAt() {
    return new Date(System.currentTimeMillis() + 30 * 86400 * 1000L);
  }
}
