package co.origon.api.model;

import co.origon.api.annotation.StyledImmutable;
import org.immutables.value.Value;

@Value.Immutable
@StyledImmutable
public abstract class AbstractOneTimeCredentials {

  public abstract String email();

  public abstract String deviceId();

  public abstract String passwordHash();

  public abstract String activationCode();
}
