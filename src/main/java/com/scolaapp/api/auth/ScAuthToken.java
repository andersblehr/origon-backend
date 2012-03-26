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
    public @Id String authToken;
    
    public String userId;
    public @Indexed String deviceId;
    public Date dateExpires;
    
    
    public ScAuthToken() {}
    
    
    public ScAuthToken(String authToken, String userId, String deviceId)
    {
        this.authToken = authToken;
        this.userId = userId;
        this.deviceId = deviceId;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 14);
        
        dateExpires = calendar.getTime();
    }
}
