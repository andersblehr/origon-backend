package co.origon.api.repository;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import com.googlecode.objectify.Key;
import java.util.Collection;

public class OrigoRepository {

  public Collection<OOrigo> fetchByKeys(Collection<Key<OOrigo>> keys) {
    return ofy().load().keys(keys).values();
  }

  public OOrigo fetchByInternalJoinCode(String internalJoinCode) {
    return (OOrigo)
        ofy()
            .load()
            .type(OReplicatedEntity.class)
            .filter("internalJoinCode", internalJoinCode)
            .first()
            .now();
  }
}
