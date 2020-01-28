package co.origon.api.repository;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.model.ofy.entity.OMembership;
import com.googlecode.objectify.Key;
import java.util.Collection;

public class MembershipRepository {

  public Collection<OMembership> fetchByKeys(Collection<Key<OMembership>> keys) {
    return ofy().load().keys(keys).values();
  }
}
