package co.origon.api;

import co.origon.api.common.Config;
import co.origon.api.model.ofy.entity.OAuthInfo;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.model.ofy.entity.ODevice;
import co.origon.api.model.ofy.entity.OMember;
import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.model.ofy.entity.OMembership;
import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.model.ofy.entity.OReplicatedEntityRef;
import com.googlecode.objectify.ObjectifyService;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
