package co.origon.api.aux;

import co.origon.api.auth.OAuthInfo;
import co.origon.api.auth.OAuthMeta;
import co.origon.api.aux.OMemberProxy;
import co.origon.api.model.ODevice;
import co.origon.api.model.OMember;
import co.origon.api.model.OMembership;
import co.origon.api.model.OOrigo;
import co.origon.api.model.OReplicatedEntity;
import co.origon.api.model.OReplicatedEntityRef;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;


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
