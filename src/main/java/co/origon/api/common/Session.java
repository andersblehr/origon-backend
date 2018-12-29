package co.origon.api.common;

import co.origon.api.OrigonApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

public class Session {
    private static Session session;
    private static final Logger LOGGER = Logger.getLogger(OrigonApplication.class.getName());

    private final String deviceId;
    private final String deviceType;
    private final String appVersion;

    public static Session create(String deviceId, String deviceType, String appVersion) {
        if (session != null) {
            throw new RuntimeException("Session instance has already been created");
        }
        return session = new Session(deviceId, deviceType, appVersion);
    }

    public static Session getSession() {
        return session;
    }

    public static void log(String message) {
        log(Level.FINE, message);
    }

    public static void log(Level level, String message) {
        if (session != null) {
            message = session.getLogPrefix() + message;
        }

        LOGGER.log(level, message);
    }

    private Session(String deviceId, String deviceType, String appVersion) {
        checkArgument( deviceId != null && deviceId.length() == 36, "Invalid or missing parameter: " + UrlParams.DEVICE_ID + ": " + deviceId);
        checkArgument(deviceType != null && deviceType.length() > 0, "Missing parameter: " + UrlParams.DEVICE_TYPE);
        checkArgument(appVersion != null && appVersion.length() > 0, "Missing parameter: " + UrlParams.APP_VERSION);

        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.appVersion = appVersion;
    }

    private String getLogPrefix() {
        return "[" + deviceId.substring(0, 8) + "/" + deviceType + "/" + appVersion + "]: ";
    }
}
