package VirtualElectionBooth.Encryption;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

;

/**
 *
 * @author adiab
 */
public class JEncryptRSA {

    public KeyPair buildKeyPair() {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = null;
        try{
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        }catch(Exception e){}
        keyPairGenerator.initialize(keySize);  
        
        return keyPairGenerator.genKeyPair();
    }

    public byte[] encrypt(Key privateKey, byte[] message){
        Cipher cipher = null;
        byte[] result = null;
        try{
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            result = cipher.doFinal(message);  
        }catch(Exception e){System.out.println(e);}

        return result;
    }
    
    public byte[] decrypt(Key publicKey, byte [] encrypted){
        Cipher cipher = null;
        byte[] result = null;
        try{
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            result = cipher.doFinal(encrypted);
        }catch(Exception e){System.out.println(e);}
        
        return result;
    }
}
