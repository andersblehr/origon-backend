package co.origon.api.helpers;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Entity
@Cache(expirationSeconds = 60)
public class Config
{
    public interface Category {
        String JWT = "jwt";
        String MAILER = "mailer";
        String MAINTENANCE_MODE = "maintenanceMode";
    }

    public interface Setting {
        // JWT settings
        String EXPIRES_IN_SECONDS = "expiresInSeconds";
        String ISSUER = "issuer";
        String SECRET = "secret";

        // Mailer settings
        String BASE_URL = "baseUrl";

        // Maintenance mode settings
        String ON = "on";
    }

    public @Id String category;
    public String configJson;

    private final static Map<String, JSONObject> configs = new HashMap<>();

    public static JSONObject get(String category) {
        if (!configs.containsKey(category)) {
            final Config config = ofy().load().key(Key.create(Config.class, category)).now();

            if (config == null) {
                throw new RuntimeException("No configuration settings for category: " + category);
            }

            configs.put(category, new JSONObject(config.configJson));
        }

        return configs.get(category);
    }
}
