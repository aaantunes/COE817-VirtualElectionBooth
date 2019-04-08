package VirtualElectionBooth.Encryption;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author diab & antunes
 */
public class JEncryptDES {
    
    public SecretKey generateKey() {
        SecretKey key = null;
        try{
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            key = keygenerator.generateKey();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return key;
    }
    
    public byte[] DESEncrypt(byte[] msg, SecretKey myDesKey){
        byte[] result = null;
        try{
            Cipher desCipher;

            // Create the cipher
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);

            //Encrypt the text
            result = desCipher.doFinal(msg);

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(NoSuchPaddingException e){
            e.printStackTrace();
        }catch(InvalidKeyException e){
            e.printStackTrace();
        }catch(IllegalBlockSizeException e){
            e.printStackTrace();
        }catch(BadPaddingException e){
            e.printStackTrace();
        }
        return result;
    }
    
    public byte[] DESDecrypt(byte[] msg, SecretKey myDesKey){
            byte[] result = null;
        try{

            Cipher desCipher;

            // Create the cipher
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, myDesKey);

            // Decrypt the text
            result = desCipher.doFinal(msg);

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(NoSuchPaddingException e){
            e.printStackTrace();
        }catch(InvalidKeyException e){
            e.printStackTrace();
        }catch(IllegalBlockSizeException e){
            e.printStackTrace();
        }catch(BadPaddingException e){
            e.printStackTrace();
        }
        return result;
    }
}
