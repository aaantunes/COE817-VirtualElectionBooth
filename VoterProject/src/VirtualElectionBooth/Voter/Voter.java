package VirtualElectionBooth.Voter;

import java.io.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.security.*;
import javax.crypto.*;
import VirtualElectionBooth.Encryption.*;

public class Voter {

    String username;
    int validationKey;
    static JEncryptDES des = new JEncryptDES();
    static JEncryptRSA rsa = new JEncryptRSA();
    private static KeyPair keyPair = rsa.buildKeyPair();
    private static PublicKey pubKey = keyPair.getPublic();
    private static PrivateKey privateKey = keyPair.getPrivate();

    /*Voter Needs:
    * X Method to pick name / id#
    * X Connect to the CLA and send name/id#
    * X Receive validation#
    * X Create msg including name/id#, validation#, vote
    * X Send voteMsg to CTF
    * */
    public Voter(String username, int validationKey){
        this.username = username;
        this.validationKey = validationKey;
    }

    public static void connectToCLA(){
        System.out.println("Connecting to CLA...");
        Scanner scanner = new Scanner(System.in);

        try (Socket socket = new Socket("localhost", 1200)) {
            System.out.println("You are now connected to CLA on Port# " + socket.getPort());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            Transmitter tr = new Transmitter();
            Frame tFrame = new Frame();
            Frame rFrame = new Frame();

            tFrame.data = pubKey.getEncoded();
            os.writeObject(tFrame);
            os.reset();
            rFrame = (Frame) is.readObject();
            PublicKey CLAPub = rFrame.getPublic();
            rFrame = (Frame) is.readObject();
            rFrame.data = rsa.decrypt(privateKey , rFrame.data);
            SecretKey desKey = rFrame.getDES();

            String username;
            String validNum;

            System.out.println("Please enter a username: ");
            username = scanner.nextLine();
            tr.send(os, username, desKey);
            if (!username.toUpperCase().equals("EXIT")){
                while (true) {
                    if ((validNum = tr.recieve(is, desKey)) != null) {
                        if (!validNum.equals("100000")){
                            System.out.println("Voters Validation Number is: " + validNum);
                            connectToCTF(createVote(username,validNum));
                            break;
                        } else {
                            System.out.println("Sorry, you already voted");
                            //this is where we could show proof they voted (decrypt "BallotList.txt")?
                            break;
                        }
                    }
                }
            } else {
                //username = "exit";
                validNum = null;
                connectToCTF(createVote(username,validNum));
                System.out.println("Exiting the VirtualElectionBooth application...");
            }

        } catch (IOException e){
            e.getMessage();
            System.out.println(e);
        } catch (Exception e){System.out.println(e);}
    }

    public static String createVote(String username, String validationKey){
        String voteMsg = "";
        if (!username.toUpperCase().equals("EXIT")){
            displayCandidates();
            Scanner scanner = new Scanner(System.in);
            int vote = scanner.nextInt();
            if (vote < 1 || vote > 2){
                System.out.println("Please enter a valid option value");
                createVote(username, validationKey);
            }
            voteMsg = username + "_" + validationKey+ "_" + vote;
        } else {
            //username = "exit"
            voteMsg = "exit";
        }
        return voteMsg;
    }

    public static void displayCandidates(){
        System.out.println("\nHi voter! The candidates running in this election are:\n" +
                "1. Andre Antunes\n" +
                "2. Ahmed Diab\n" +
                "To vote please enter the candidates respective value!");
    }

    public static void connectToCTF(String vote){
        System.out.println("\nConnecting to CTF...");

        try (Socket socket = new Socket("localhost", 1201)) {
            System.out.println("You are now connected to CTF on Port# " + socket.getPort() + "\n");
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            Transmitter tr = new Transmitter();
            Frame tFrame = new Frame();
            Frame rFrame = new Frame();
            JEncryptDES des = new JEncryptDES();
            SecretKey SessionKey = des.generateKey();

            rFrame = (Frame) is.readObject();
            PublicKey CTFPub = rFrame.getPublic();
            tFrame.data = pubKey.getEncoded();
            os.writeObject(tFrame);
            os.reset();
            byte[] encrypted = rsa.encrypt(CTFPub , SessionKey.getEncoded());
            tFrame.data = encrypted;
            os.writeObject(tFrame);
            os.reset();

            System.out.println("Sending vote: " + vote);
            tr.send(os, vote, SessionKey);
        } catch (IOException e){
            e.getMessage();
        } catch (Exception e){System.out.println(e);}
    }

    public static void main(String[] args) {
        connectToCLA();
    }
}