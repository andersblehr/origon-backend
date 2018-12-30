package co.origon.api.model.ofy.entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Entity
@Cache(expirationSeconds = 600)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OAuthInfo {
    @Id
    private String email;
    private String deviceId;
    private String passwordHash;
    private String activationCode;

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
