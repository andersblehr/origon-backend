package co.origon.api.model.client.ofy;

import co.origon.api.model.client.ReplicatedEntityRef;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Subclass;
import lombok.NoArgsConstructor;

@Subclass
@NoArgsConstructor
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "referencedEntityKey"},
    ignoreUnknown = true)
public class OReplicatedEntityRef extends OReplicatedEntity {

  public @Ignore String referencedEntityId;
  public @Ignore String referencedEntityOrigoId;
  public Key<OReplicatedEntity> referencedEntityKey;

  public OReplicatedEntityRef(ReplicatedEntityRef entityRef) {
    super(entityRef);
    referencedEntityId = entityRef.referencedEntityId();
    referencedEntityOrigoId = entityRef.referencedEntityOrigoId();
  }

  @Override
  public ReplicatedEntityRef fromOfy() {
    return ReplicatedEntityRef.builder()
        .entityId(entityId)
        .origoId(origoId)
        .entityClass(entityClass)
        .referencedEntityId(referencedEntityId)
        .referencedEntityOrigoId(referencedEntityOrigoId)
        .createdBy(createdBy)
        .dateCreated(dateCreated)
        .modifiedBy(modifiedBy)
        .dateReplicated(dateReplicated)
        .isExpired(isExpired)
        .build();
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
