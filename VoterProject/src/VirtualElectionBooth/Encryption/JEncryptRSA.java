package coe817;

import java.security.*;
import javax.crypto.Cipher;

/**
 *
 * @author adiab
 */
public class JEncryptRSA {

    public static KeyPair buildKeyPair() {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = null;
        try{
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        }catch(Exception e){}
        keyPairGenerator.initialize(keySize);  
        
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(Key privateKey, byte[] message){
        Cipher cipher = null;
        byte[] result = null;
        try{
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            result = cipher.doFinal(message);  
        }catch(Exception e){}

        return result;
    }
    
    public static byte[] decrypt(Key publicKey, byte [] encrypted){
        Cipher cipher = null;
        byte[] result = null;
        try{
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            result = cipher.doFinal(encrypted);
        }catch(Exception e){}
        
        return result;
    }
}
