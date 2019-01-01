package co.origon.api.common;

import com.googlecode.objectify.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.json.JSONException;
import org.json.JSONObject;


@Entity
@Cache(expirationSeconds = 60)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public class Config implements co.origon.api.model.api.entity.Config {

    @Id private String category;
    private String configJson;
    @IgnoreSave private JSONObject jsonObject;

    @Override
    public String getString(String setting) {
        return jsonObject.getString(setting);
    }

    @Override
    public int getInt(String setting) {
        return jsonObject.getInt(setting);
    }

    @OnLoad
    private void loadJsonObject() {
        try {
            jsonObject = new JSONObject(configJson);
        } catch (JSONException e) {
            throw new RuntimeException("Could not load JSON object from datastore", e);
        }
    }
}
