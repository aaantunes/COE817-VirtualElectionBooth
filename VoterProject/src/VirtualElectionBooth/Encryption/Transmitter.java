package VirtualElectionBooth.Encryption;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;

public class Transmitter {
    byte[] b;
    JEncryptDES des = new JEncryptDES();
    Frame frame = new Frame();

    public void send(ObjectOutputStream os, String string, SecretKey key) throws Exception{
        frame.data = des.DESEncrypt(string.getBytes(), key);
        os.writeObject(frame);
        os.reset();
    }
    public String recieve(ObjectInputStream is, SecretKey key) throws Exception{
        frame =  (Frame) is.readObject();
        return new String(des.DESDecrypt(frame.data, key));
    }
}
