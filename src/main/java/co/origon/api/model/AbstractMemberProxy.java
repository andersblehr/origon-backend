package co.origon.api.model;

import co.origon.api.annotation.StyledImmutable;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
@StyledImmutable
public abstract class AbstractMemberProxy {

  public abstract String id();

  @Nullable
  public abstract String memberId();

  public abstract Optional<String> memberName();

  public abstract Optional<String> passwordHash();

  public abstract Set<String> deviceTokens();

  public abstract Set<EntityKey> membershipKeys();

  public boolean isRegistered() {
    return passwordHash().map(hash -> hash.length() > 0).orElse(false);
  }
}
