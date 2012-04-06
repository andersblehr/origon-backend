package com.scolaapp.api.auth;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
@Unindexed
@Cached(expirationSeconds=600)
public class ScAuthToken
{
    public @Id String token;
    
    public @Indexed String userId;
    public String scolaId;
    public String deviceId;
    public String deviceType;
    
    public Date dateExpires;
    
    
    public ScAuthToken() {}
    
    
    public ScAuthToken(String token, String userId, String scolaId, String deviceId, String deviceType)
    {
        this.token = token;
        
        this.userId = userId;
        this.scolaId = scolaId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 14);
        
        dateExpires = calendar.getTime();
    }
    
    
    public ScAuthToken(String token, ScAuthToken oldAuthToken)
    {
        this(token, oldAuthToken.userId, oldAuthToken.scolaId, oldAuthToken.deviceId, oldAuthToken.deviceType);
    }
}
