package co.origon.api.common;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Settings {
  private static final String JWT_CFG = "origon.jwt";
  public static final String JWT_ISSUER = "issuer";
  public static final String JWT_SECRET = "secret";
  public static final String JWT_EXPIRES_IN_SECONDS = "expiresInSeconds";

  private static final String MAILER_CFG = "origon.mailer";
  public static final String MAILER_BASE_URL = "baseUrl";

  private static final String SYSTEM_CFG = "origon.system";
  public static final String SYSTEM_STATUS = "status";
  public static final String SYSTEM_STATUS_OK = "ok";
  public static final String SYSTEM_STATUS_DOWN = "down";

  private static final Config jwt;
  private static final Config mailer;
  private static final Config system;

  static {
    jwt = ConfigFactory.load().getConfig(JWT_CFG);
    mailer = ConfigFactory.load().getConfig(MAILER_CFG);
    system = ConfigFactory.load().getConfig(SYSTEM_CFG);
  }

  public static Config jwt() {
    return jwt;
  }

  public static Config mailer() {
    return mailer;
  }

  public static Config system() {
    return system;
  }
}
