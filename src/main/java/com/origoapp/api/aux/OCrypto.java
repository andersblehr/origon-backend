package com.origoapp.api.aux;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;


public class OCrypto
{
    private static final String kOrigoSeasoning = "socroilgao";
    
    
    public static String hashUsingSHA1(String inputString)
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
            OLog.log().severe(String.format("Caught exception while generating SHA1 hash from input string '%s', bailing out.", inputString));
            throw new WebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        return output;
    }
    
    
    public static String generatePasswordHash(String password, String email)
    {
        String passwordHash = null;
        
        if ((password != null) && (email != null)) {
            passwordHash = OCrypto.hashUsingSHA1(OCrypto.seasonedString(password)); 
        }
        
        return passwordHash; 
    }
    
    
    public static String generateTimestampHash(String timestamp)
    {
        String timestampHash = null;
        
        if (timestamp != null) {
            timestampHash = OCrypto.hashUsingSHA1(OCrypto.seasonedString(timestamp));
        }
        
        return timestampHash;
    }


    private static String seasonedString(String string)
    {
        String stringHash = hashUsingSHA1(string);
        String seasoningHash = hashUsingSHA1(kOrigoSeasoning);
        
        int hashLength = stringHash.length();
        
        byte[] stringBytes = stringHash.getBytes();
        byte[] seasoningBytes = seasoningHash.getBytes();
        byte[] seasonedBytes = new byte[hashLength];
        
        for (int i = 0; i < hashLength; i++) {
            byte char1 = stringBytes[i];
            byte char2 = seasoningBytes[hashLength - (i + 1)];
            
            if (char1 >= char2) {
                seasonedBytes[i] = (byte)(char1 - char2 + 33); // ASCII 33 = '!'
            } else {
                seasonedBytes[i] = (byte)(char2 - char1 + 33);
            }
        }
        
        return new String(seasonedBytes);
    }
}
