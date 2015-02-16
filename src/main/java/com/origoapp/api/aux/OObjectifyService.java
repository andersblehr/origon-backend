package com.origoapp.api.aux;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import com.origoapp.api.auth.OAuthInfo;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.aux.OMemberProxy;

import com.origoapp.api.model.ODevice;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OOrigo;
import com.origoapp.api.model.OReplicatedEntity;
import com.origoapp.api.model.OReplicatedEntityRef;


public class OObjectifyService
{
    static
    {
        ObjectifyService.register(OAuthInfo.class);
        ObjectifyService.register(OAuthMeta.class);
        ObjectifyService.register(OMemberProxy.class);
        ObjectifyService.register(OReplicatedEntity.class);
        
        ObjectifyService.register(ODevice.class);
        ObjectifyService.register(OMember.class);
        ObjectifyService.register(OMembership.class);
        ObjectifyService.register(OOrigo.class);
        ObjectifyService.register(OReplicatedEntityRef.class);
    }
    
    
    public static Objectify ofy()
    {
        return ObjectifyService.ofy();
    }
}
