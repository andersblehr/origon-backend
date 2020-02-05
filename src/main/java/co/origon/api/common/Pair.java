package co.origon.api.common;

import java.util.Optional;
import lombok.Data;
import lombok.experimental.Accessors;

public class Pair<T, U> {
  private final T left;
  private final U right;

  public static <T, U> Pair<T, U> of(T left, U right) {
    return new Pair<>(left, right);
  }

  public T left() {
    return left;
  }

  public Optional<U> right() {
    return Optional.ofNullable(right);
  }

  private Pair(T left, U right) {
    this.left = left;
    this.right = right;
  }
}
