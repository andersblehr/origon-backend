package co.origon.api.common;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class InputValidator {

    public static void checkReplicationDate(Date replicationDate) {
        try {
            checkNotNull(replicationDate, "Missing HTTP header: " + HttpHeaders.IF_MODIFIED_SINCE);
            checkArgument(replicationDate.before(new Date()), "Invalid last replication date: " + replicationDate);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    public static String checkMetadata(String deviceId, String deviceType, String appVersion) {
        try {
            return String.format("[%s] %s/%s: ",
                    checkNotNull(deviceId, "Missing parameter: " + UrlParams.DEVICE_ID),
                    checkNotNull(deviceType, "Missing parameter: " + UrlParams.DEVICE_TYPE),
                    checkNotNull(appVersion, "Missing parameter: " + UrlParams.APP_VERSION)
            );
        } catch (NullPointerException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    public static void checkLanguage(String language) {
        try {
            checkNotNull(language, "Missing parameter: " + UrlParams.LANGUAGE);
            checkArgument(Arrays.asList(Mailer.LANGUAGES).contains(language), "Unsupported language: " + language);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }
}
