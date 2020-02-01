package co.origon.api.model.api.entity;

import java.util.Date;

public interface Device {

  String type();

  String name();

  Date lastSeen();

  Member user();
}
