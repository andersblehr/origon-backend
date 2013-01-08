package com.origoapp.api.aux;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import com.origoapp.api.auth.OAuthInfo;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.aux.OMemberProxy;

import com.origoapp.api.model.ODevice;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMemberResidency;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OMessageBoard;
import com.origoapp.api.model.OOrigo;
import com.origoapp.api.model.OReplicatedEntityGhost;
import com.origoapp.api.model.OReplicatedEntityRef;


public class OObjectifyService
{
    static
    {
        ObjectifyService.factory().register(OAuthInfo.class);
        ObjectifyService.factory().register(OAuthMeta.class);
        ObjectifyService.factory().register(OMemberProxy.class);
        
        ObjectifyService.factory().register(ODevice.class);
        ObjectifyService.factory().register(OMember.class);
        ObjectifyService.factory().register(OMemberResidency.class);
        ObjectifyService.factory().register(OMembership.class);
        ObjectifyService.factory().register(OMessageBoard.class);
        ObjectifyService.factory().register(OOrigo.class);
        ObjectifyService.factory().register(OReplicatedEntityGhost.class);
        ObjectifyService.factory().register(OReplicatedEntityRef.class);
    }
    
    
    public static Objectify ofy()
    {
        return ObjectifyService.ofy();
    }
}
