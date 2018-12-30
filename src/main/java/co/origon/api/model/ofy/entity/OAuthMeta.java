package co.origon.api.model.ofy.entity;

import java.util.Date;

import co.origon.api.model.api.entity.DeviceCredentials;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.*;
import lombok.experimental.Accessors;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Entity
@Cache(expirationSeconds = 600)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public class OAuthMeta implements DeviceCredentials {

    @Id private String authToken;
    private String email;
    private String deviceId;
    private String deviceType;
    private Date dateExpires = dateExpires();

    public static OAuthMeta get(String authToken) {
        if (authToken == null) {
            throw new NullPointerException("Auth token is null");
        }
        if (authToken.length() != 40) {
            throw new IllegalArgumentException("Invalid auth token: " + authToken);
        }

        return ofy().load().type(OAuthMeta.class).id(authToken).now();
    }

    public void save() {
        ofy().save().entity(this).now();
    }

    public void delete() {
        ofy().delete().entity(this);
    }

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
        return new Date(System.currentTimeMillis() + 30 * 86400 * 1000L);
    }
}
