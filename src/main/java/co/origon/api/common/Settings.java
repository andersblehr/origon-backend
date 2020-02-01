package co.origon.api.common;

import com.typesafe.config.ConfigFactory;

public class Config {
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

  private static com.typesafe.config.Config jwt;
  private static com.typesafe.config.Config mailer;
  private static com.typesafe.config.Config system;

  static {
    jwt = ConfigFactory.load().getConfig(JWT_CFG);
    mailer = ConfigFactory.load().getConfig(MAILER_CFG);
    system = ConfigFactory.load().getConfig(SYSTEM_CFG);
  }

  public static com.typesafe.config.Config jwt() {
    return jwt;
  }

  public static com.typesafe.config.Config mailer() {
    return mailer;
  }

  public static com.typesafe.config.Config system() {
    return system;
  }
}
