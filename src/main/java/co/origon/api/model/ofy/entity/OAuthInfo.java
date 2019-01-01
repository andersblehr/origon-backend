package co.origon.api.model.ofy.entity;

import co.origon.api.model.api.entity.OtpCredentials;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Cache(expirationSeconds = 600)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(fluent = true)
public class OAuthInfo implements OtpCredentials {
    @Id private String email;
    private String deviceId;
    private String passwordHash;
    private String activationCode;
}
