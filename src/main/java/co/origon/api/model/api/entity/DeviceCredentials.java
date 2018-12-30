package co.origon.api.model.api.entity;

import co.origon.api.model.api.Entity;

import java.util.Date;

public interface DeviceCredentials extends Entity {

    DeviceCredentials email(String email);
    DeviceCredentials deviceToken(String deviceToken);
    DeviceCredentials deviceId(String deviceId);
    DeviceCredentials deviceType(String deviceType);

    String email();
    String deviceToken();
    String deviceId();
    String deviceType();
    Date dateExpires();
}
