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
    
    public @Indexed String deviceId;
    public String userId;
    public Date dateExpires;
    
    
    public ScAuthToken() {}
    
    
    public ScAuthToken(String authToken, String deviceId, String userId)
    {
        this.authToken = authToken;
        this.deviceId = deviceId;
        this.userId = userId;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 14);
        
        dateExpires = calendar.getTime();
    }
}
