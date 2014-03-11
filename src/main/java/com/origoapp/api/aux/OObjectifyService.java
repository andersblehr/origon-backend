package com.origoapp.api.aux;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import com.origoapp.api.auth.OAuthInfo;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.aux.OMemberProxy;

import com.origoapp.api.model.ODevice;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OResidencySchedule;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OOrigo;
import com.origoapp.api.model.OReplicatedEntityRef;
import com.origoapp.api.model.OSettings;


public class OObjectifyService
{
    static
    {
        ObjectifyService.factory().register(OAuthInfo.class);
        ObjectifyService.factory().register(OAuthMeta.class);
        ObjectifyService.factory().register(OMemberProxy.class);
        
        ObjectifyService.factory().register(ODevice.class);
        ObjectifyService.factory().register(OMember.class);
        ObjectifyService.factory().register(OResidencySchedule.class);
        ObjectifyService.factory().register(OMembership.class);
        ObjectifyService.factory().register(OOrigo.class);
        ObjectifyService.factory().register(OReplicatedEntityRef.class);
        ObjectifyService.factory().register(OSettings.class);
    }
    
    
    public static Objectify ofy()
    {
        return ObjectifyService.ofy();
    }
}
