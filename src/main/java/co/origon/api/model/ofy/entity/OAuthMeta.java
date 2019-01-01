package co.origon.api.model.ofy.entity;

import java.util.Date;

import co.origon.api.model.api.entity.DeviceCredentials;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;


@Entity
@Cache(expirationSeconds = 600)
@Data
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class OAuthMeta implements DeviceCredentials {

    @Id private String authToken;
    private String email;
    private String deviceId;
    private String deviceType;
    private Date dateExpires = dateExpires();

    @Override
    public DeviceCredentials deviceToken(String deviceToken) {
        authToken = deviceToken;
        return this;
    }

    @Override
    public String deviceToken() {
        return authToken;
    }

    @Override
    public Date dateExpires() {
        if (dateExpires == null)
            dateExpires = new Date(System.currentTimeMillis() + 30 * 86400 * 1000L);
        return dateExpires;
    }
}
