package co.origon.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import co.origon.api.common.Config;

import co.origon.api.model.ofy.entity.*;
import com.googlecode.objectify.ObjectifyService;

public class OrigonBootstrapper implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    ObjectifyService.init();

    ObjectifyService.register(Config.class);

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
