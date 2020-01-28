package co.origon.api.repository;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.model.ofy.entity.OMemberProxy;
import com.googlecode.objectify.Key;
import java.util.Collection;
import java.util.stream.Collectors;

public class MemberProxyRepository {
  public OMemberProxy fetchById(String id) {
    return ofy().load().key(keyFromId(id)).now();
  }

  public Collection<OMemberProxy> fetchByKeys(Collection<Key<OMemberProxy>> keys) {
    return ofy().load().keys(keys).values();
  }

  public void deleteByIds(Collection<String> ids) {
    ofy().delete().keys(keysFromIds(ids));
  }

  private Collection<Key<OMemberProxy>> keysFromIds(Collection<String> ids) {
    return ids.stream().map(this::keyFromId).collect(Collectors.toList());
  }

  private Key<OMemberProxy> keyFromId(String id) {
    return Key.create(OMemberProxy.class, id);
  }
}
