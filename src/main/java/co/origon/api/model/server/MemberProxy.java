package co.origon.api.model.server;

import co.origon.api.model.EntityKey;
import java.util.Set;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@With
@Builder
@Accessors(fluent = true)
public class MemberProxy {

  private final String id;
  private final String memberId;
  private final String memberName;
  private final String passwordHash;
  @Singular private final Set<String> deviceTokens;
  @Singular private final Set<EntityKey> membershipKeys;

  public boolean isRegistered() {
    return passwordHash != null && passwordHash.length() > 0;
  }
}
