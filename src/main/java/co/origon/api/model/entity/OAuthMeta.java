package co.origon.api.model.entity;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.*;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Entity
@Cache(expirationSeconds = 600)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OAuthMeta {
    @Id
    private String authToken;

    @Setter
    private String email;
    private String deviceId;
    private String deviceType;

    @Builder.Default
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

    private static Date dateExpires() {
        return new Date(System.currentTimeMillis() + 30 * 86400 * 1000L);
    }
}
