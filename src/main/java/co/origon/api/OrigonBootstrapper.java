package co.origon.api;

import co.origon.api.model.client.ofy.ODevice;
import co.origon.api.model.client.ofy.OMember;
import co.origon.api.model.client.ofy.OMembership;
import co.origon.api.model.client.ofy.OOrigo;
import co.origon.api.model.client.ofy.OReplicatedEntity;
import co.origon.api.model.client.ofy.OReplicatedEntityRef;
import co.origon.api.model.server.ofy.OAuthInfo;
import co.origon.api.model.server.ofy.OAuthMeta;
import co.origon.api.model.server.ofy.OMemberProxy;
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
