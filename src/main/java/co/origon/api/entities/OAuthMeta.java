package co.origon.api.entities;

import java.util.Calendar;
import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Entity
@Cache(expirationSeconds = 600)
@Builder
@Getter
public class OAuthMeta {
    @Id
    private final String authToken;

    @Setter
    private String email;
    private final String deviceId;
    private final String deviceType;

    @Getter(lazy = true)
    private final Date dateExpires = dateExpires();

    public static OAuthMeta get(String authToken) {
        if (authToken == null || authToken.length() != 40) {
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

    private Date dateExpires() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 30);

        return calendar.getTime();
    }
}
