package co.origon.api.common;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Cache(expirationSeconds = 60)
public class Config {

    public interface Category {
        String JWT = "jwt";
        String MAILER = "mailer";
        String SYSTEM = "system";
    }

    public interface Setting {
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

    public @Id String category;
    public String configJson;

    private final static Map<String, JSONObject> configs = new HashMap<>();

    public static void create(String category, String configJson) {
        if (configs.containsKey(category)) {
            delete(category);
        }

        final Config config = Config.builder()
                .category(category)
                .configJson(new JSONObject(configJson).toString())
                .build();

        ofy().save().entity(config).now();
        configs.put(category, new JSONObject(configJson));
    }

    public static JSONObject get(String category) throws JSONException {
        if (!configs.containsKey(category)) {
            final Config config = ofy().load().key(Key.create(Config.class, category)).now();
            if (config != null) {
                configs.put(category, new JSONObject(config.configJson));
            }
        }

        return configs.get(category);
    }

    public static void delete(String category) {
        if (configs.containsKey(category)) {
            final Config config = ofy().load().key(Key.create(Config.class, category)).now();
            ofy().delete().entity(config).now();
            configs.remove(category);
        }
    }
}
