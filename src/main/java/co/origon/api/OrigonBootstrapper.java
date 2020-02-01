package co.origon.api;

import co.origon.api.model.ofy.OAuthInfo;
import co.origon.api.model.ofy.OAuthMeta;
import co.origon.api.model.ofy.ODevice;
import co.origon.api.model.ofy.OMember;
import co.origon.api.model.ofy.OMemberProxy;
import co.origon.api.model.ofy.OMembership;
import co.origon.api.model.ofy.OOrigo;
import co.origon.api.model.ofy.OReplicatedEntity;
import co.origon.api.model.ofy.OReplicatedEntityRef;
import com.googlecode.objectify.ObjectifyService;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OrigonBootstrapper implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    ObjectifyService.init();

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

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}
}
