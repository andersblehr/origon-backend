package co.origon.api.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.OnLoad;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.json.JSONException;
import org.json.JSONObject;

@Entity
@Cache(expirationSeconds = 60)
@Data
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Config implements co.origon.api.model.api.entity.Config {

  @Id @Setter private String category;
  private String configJson;
  @IgnoreSave private JSONObject jsonObject;

  @Override
  public co.origon.api.model.api.entity.Config configJson(String configJson) {
    this.configJson = configJson;
    loadJsonObject();
    return this;
  }

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
