package co.origon.api.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data(staticConstructor = "of")
public class Pair<T, U> {
  private final T left;
  private final U right;
}
