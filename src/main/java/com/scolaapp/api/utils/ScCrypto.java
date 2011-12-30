package com.scolaapp.api.utils;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;


public class ScCrypto
{
    private static String diffStrings(String string1, String string2)
    {
        if (string1.length() != string2.length()) {
            ScAppEnv.getLog().severe(String.format("BROKEN: Cannot diff strings of different lengths. (%s vs. %s)", string1, string2));
            throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        int stringLength = string1.length();
        
        byte[] byteString1 = string1.getBytes();
        byte[] byteString2 = string2.getBytes();
        byte[] diffedBytes = new byte[stringLength];
        
        for (int i = 0; i < stringLength; i++) {
            byte char1 = byteString1[i];
            byte char2 = byteString2[stringLength - (i + 1)];
            
            if (char1 >= char2) {
                diffedBytes[i] = (byte)(char1 - char2 + 33); // ASCII 33 = '!'
            } else {
                diffedBytes[i] = (byte)(char2 - char1 + 33);
            }
        }
        
        return new String(diffedBytes);
    }
    
    
    private static String hashUsingSHA1(String inputString)
    {
        String output = new String();
        
        try {
            byte[] bytes = inputString.getBytes("UTF-8");

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] digestOutput = digest.digest(bytes);
            
            for (int i = 0; i < digest.getDigestLength(); i++) {
                output = output.concat(String.format("%02x", digestOutput[i]));
            }
        } catch (Exception e) {
            ScAppEnv.getLog().severe(String.format("Caught exception while generating SHA1 hash from input string '%s', bailing out.", inputString));
            throw new WebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        return output;
    }
    
    
    public static String generatePasswordHash(String password, String UUID)
    {
        String unsaltedPasswordHash = ScCrypto.hashUsingSHA1(password);
        String unsaltedUUIDHash = ScCrypto.hashUsingSHA1(UUID);
        String saltyDiff = ScCrypto.diffStrings(unsaltedPasswordHash, unsaltedUUIDHash);
        
        return ScCrypto.hashUsingSHA1(saltyDiff);
    }
}
