package com.scolaapp.api.auth;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScAuthTokenMeta
{
    public @Id String authToken;
    
    public String userId;
    public String scolaId;
    public String deviceId;
    public String deviceType;
    
    public Date dateExpires;
    
    
    public ScAuthTokenMeta() {}
    
    
    public ScAuthTokenMeta(String authToken, String userId, String scolaId, String deviceId, String deviceType)
    {
        this.authToken = authToken;
        
        this.userId = userId;
        this.scolaId = scolaId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 14);
        
        dateExpires = calendar.getTime();
    }
}
