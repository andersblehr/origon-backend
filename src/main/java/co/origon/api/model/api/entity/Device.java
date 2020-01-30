package co.origon.api.model.api.entity;

import java.time.Instant;

public interface Device {

  String type();

  String name();

  Instant lastSeen();

  Member user();
}
