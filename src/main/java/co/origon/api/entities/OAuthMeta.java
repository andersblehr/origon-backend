package co.origon.api.entities;

import java.util.Calendar;
import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


@Entity
@Cache(expirationSeconds = 600)
public class OAuthMeta
{
    public @Id String authToken;
    
    public String email;
    public String deviceId;
    public String deviceType;
    
    public Date dateExpires;
    
    
    public OAuthMeta() {}
    
    
    public OAuthMeta(String authToken, String email, String deviceId, String deviceType)
    {
        this.authToken = authToken;
        
        this.email = email;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 31);
        
        dateExpires = calendar.getTime();
    }
}
