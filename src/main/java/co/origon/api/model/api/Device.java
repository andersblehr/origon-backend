package co.origon.api.model.api;

import java.util.Date;

public interface Device {

  String type();

  String name();

  Date lastSeen();

  Member user();
}
