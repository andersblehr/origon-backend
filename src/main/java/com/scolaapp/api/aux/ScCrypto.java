package com.scolaapp.api.aux;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;


public class ScCrypto
{
    private static String diffStrings(String string1, String string2)
    {
        String string1Hash = hashUsingSHA1(string1);
        String string2Hash = hashUsingSHA1(string2);
        
        int hashLength = string1Hash.length();
        
        byte[] byteString1 = string1Hash.getBytes();
        byte[] byteString2 = string2Hash.getBytes();
        byte[] diffedBytes = new byte[hashLength];
        
        for (int i = 0; i < hashLength; i++) {
            byte char1 = byteString1[i];
            byte char2 = byteString2[hashLength - (i + 1)];
            
            if (char1 >= char2) {
                diffedBytes[i] = (byte)(char1 - char2 + 33); // ASCII 33 = '!'
            } else {
                diffedBytes[i] = (byte)(char2 - char1 + 33);
            }
        }
        
        return new String(diffedBytes);
    }
    
    
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
            ScLog.log().severe(String.format("Caught exception while generating SHA1 hash from input string '%s', bailing out.", inputString));
            throw new WebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        return output;
    }
    
    
    public static String generatePasswordHash(String password, String email)
    {
        String passwordHash = null;
        
        if ((password != null) && (email != null)) {
            String saltyDiff = ScCrypto.diffStrings(password, email);
            passwordHash = ScCrypto.hashUsingSHA1(saltyDiff); 
        }
        
        return passwordHash; 
    }
}
