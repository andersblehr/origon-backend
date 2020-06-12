package co.origon.api.model.server;

import co.origon.api.model.EntityKey;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
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
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class MemberProxy {

  String id;
  String memberId;
  String memberName;
  String passwordHash;
  @Singular Set<String> deviceTokens;
  @Singular Set<EntityKey> membershipKeys;

  public boolean isRegistered() {
    return passwordHash != null && passwordHash.length() > 0;
  }
}
