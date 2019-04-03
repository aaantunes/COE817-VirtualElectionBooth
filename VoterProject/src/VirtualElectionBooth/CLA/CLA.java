package VirtualElectionBooth.CLA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;

public class CLA {

    /* CLA Goals:
    * X receiver connection from voter
    * X create list of validation#'s with name/id#
    * X if voter connects and his name is on the list, return valid#
    * X else, create validation# for voter w/ name/id# and,
    * X add valid# and name/id# to list
    * X then send valid# to voter
    * send list to CTF
    */

    public static void main(String[] args) {
        //Set up connection with voter
        try (ServerSocket serverSocket = new ServerSocket(1200)){
            System.out.println("CLAServer Starting...\nWaiting for connections...");

            while(true){
                new CLAServer(serverSocket.accept()).start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

class CLAServer extends Thread{
    private Socket socket;

    private static Hashtable<String, Integer> voterList = new Hashtable<>();

    public CLAServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try{
            currentThread().setName("CLA Thread");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            System.out.println("Voter connected on: " +currentThread().getId());

            String username;
            int validationKey;
            String receivedMsg;

            while((receivedMsg = in.readLine()) != null){
                username = receivedMsg;
                System.out.println("Received: " + username);
                validationKey = checkUserValidation(username);
                out.println(validationKey);
                System.out.println("Sent: " + validationKey);
            }

            System.out.println(voterList);

        } catch (IOException e){
            e.getMessage();
        } finally {
            try{
                socket.close();
            } catch (IOException e){}
        }
    }

    /* If user is valid, call getValidNum
     * Else, Create validNum, add to array list, and call sendToCTF */
    public int checkUserValidation(String username){
        if (voterList.containsKey(username)){
            return voterList.get(username);
        } else {
            voterList.put(username, createValidNum());
            //CONNECT TO CTF AND SEND USERNAME_VALIDNUMBER
            String voterMsg = username + "_" + voterList.get(username);
            System.out.println("TESTING: " + voterMsg);
            connectToCTF(voterMsg);
            return voterList.get(username);
        }
    }

    public int createValidNum(){
        int validNum;
        do {
            Random random = new Random();
            validNum = random.nextInt(100000);
        } while(voterList.contains(validNum));

        return validNum;
    }

    public static void connectToCTF(String voterMsg){
        System.out.println("Connecting to CTF...");

        try (Socket socket = new Socket("localhost", 1201)) {
            System.out.println("You are now connected to CTF on Port# " + socket.getPort());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("TESTING connectToCTF: " + voterMsg);
            out.println(voterMsg);
//            while (true) {
//                if ((validNum = in.readLine()) != null) {
//                    System.out.println("Valid Num is: " + validNum);
//                    break;
//                }
//            }
        } catch (IOException e){
            e.getMessage();
        }
    }
}