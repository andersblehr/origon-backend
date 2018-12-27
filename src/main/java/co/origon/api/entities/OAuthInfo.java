package co.origon.api.entities;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Builder;
import lombok.Getter;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Entity
@Cache(expirationSeconds = 600)
@Builder
@Getter
public class OAuthInfo {
    @Id
    private final String email;
    private final String deviceId;
    private final String passwordHash;
    private final String activationCode;

    public static OAuthInfo get(String email) {
        if (email == null || email.length() == 0) {
            throw new IllegalArgumentException("Email address is missing");
        }

        return ofy().load().type(OAuthInfo.class).id(email).now();
    }

    public void save() {
        ofy().save().entity(this).now();
    }

    public void delete() {
        ofy().delete().entity(this);
    }
}
