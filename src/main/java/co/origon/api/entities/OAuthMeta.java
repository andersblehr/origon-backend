package co.origon.api.entities;

import java.util.Calendar;
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
