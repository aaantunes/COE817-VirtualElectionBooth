package VirtualElectionBooth.Encryption;

import java.io.Serializable;
import java.security.*;
import javax.crypto.*;
import java.security.spec.*;
import javax.crypto.spec.SecretKeySpec;

public class Frame implements Serializable {
    public byte[] data;

    public PublicKey getPublic(){
        PublicKey key = null;
        X509EncodedKeySpec ks = new X509EncodedKeySpec(data);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            key = kf.generatePublic(ks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public SecretKey getDES(){
        return new SecretKeySpec(data, 0, data.length, "DES");
    }
}