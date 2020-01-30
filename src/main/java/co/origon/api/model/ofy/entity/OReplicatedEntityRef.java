package co.origon.api.model.ofy.entity;

import co.origon.api.model.EntityKey;
import co.origon.api.model.api.entity.ReplicatedEntityRef;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Subclass;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "referencedEntityKey"},
    ignoreUnknown = true)
public class OReplicatedEntityRef extends OReplicatedEntity implements ReplicatedEntityRef {
  public @Ignore String referencedEntityId;
  public @Ignore String referencedEntityOrigoId;
  public Key<OReplicatedEntity> referencedEntityKey;

  public OReplicatedEntityRef() {
    super();
  }

  @Override
  public EntityKey referencedEntityKey() {
    return EntityKey.from(referencedEntityId, referencedEntityOrigoId);
  }

  @OnSave
  @Override
  public void internaliseRelationships() {
    super.internaliseRelationships();

    referencedEntityKey =
        Key.create(
            Key.create(OOrigo.class, referencedEntityOrigoId),
            OReplicatedEntity.class,
            referencedEntityId);
  }

  @OnLoad
  @Override
  public void externaliseRelationships() {
    super.externaliseRelationships();

    referencedEntityId = referencedEntityKey.getRaw().getName();
    referencedEntityOrigoId = referencedEntityKey.getParent().getRaw().getName();
  }
}
