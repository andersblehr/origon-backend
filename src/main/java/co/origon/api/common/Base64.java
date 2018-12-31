package co.origon.api.common;

public class Base64 {

    public static String encode(String toBeEncoded) {
        return new String(java.util.Base64.getEncoder().encode(toBeEncoded.getBytes()));
    }

    public static String decode(String toBeDecoded) {
        return new String(java.util.Base64.getDecoder().decode(toBeDecoded.getBytes()));
    }
}
