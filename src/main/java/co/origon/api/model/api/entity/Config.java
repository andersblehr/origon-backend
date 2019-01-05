package co.origon.api.model.api.entity;

import co.origon.api.model.api.Entity;

public interface Config extends Entity<Config> {
    interface Category {
        String JWT = "jwt";
        String MAILER = "mailer";
        String SYSTEM = "system";
    }

    interface Setting {
        // JWT setting keys
        String EXPIRES_IN_SECONDS = "expiresInSeconds";
        String ISSUER = "issuer";
        String SECRET = "secret";

        // Mailer setting keys
        String BASE_URL = "baseUrl";

        // System setting keys
        String STATUS = "status";
        // System settings
        String STATUS_OK = "OK";
    }

    Config category(String category);
    Config configJson(String configJson);

    String category();
    String configJson();

    String getString(String setting);
    int getInt(String setting);
}
