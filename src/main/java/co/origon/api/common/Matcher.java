package co.origon.api.common;

public class Matcher {
  public static boolean isEmailAddress(String maybeEmail) {
    return maybeEmail.matches("^.+@.+\\..+$");
  }

  public static boolean isDeviceToken(String maybeDeviceToken) {
    return maybeDeviceToken.matches("^[a-z0-9]{40}$");
  }
}
