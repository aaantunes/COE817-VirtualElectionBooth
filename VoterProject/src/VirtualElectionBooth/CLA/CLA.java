package VirtualElectionBooth.CLA;

import java.io.*;
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
    * X send list to CTF
    */

    public static void main(String[] args) {
        //Set up connection with voter
        try (ServerSocket serverSocket = new ServerSocket(1200)){
            System.out.println("CLAServer Starting...\nWaiting for connections...");
            System.out.println("\nUpdating Voter List...\n");

            while(true){
                new CLAServer(serverSocket.accept()).start();
                //no way to exit application bc of this while loop
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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            String username;
            int validationKey;
            String receivedMsg;

            updateVoterListWithBallots();

            while((receivedMsg = in.readLine()) != null){
                System.out.println("--------------------------------------------");
                username = receivedMsg;
                 if (username.toUpperCase().equals("EXIT")){
                     System.out.println("The election is over...");
                     break;
                 } else {
                     System.out.println("Received voter's username: " + username);
                     validationKey = checkUserValidation(username);
                     out.println(validationKey);
                     if (validationKey != 100000){
                         System.out.println("Sent voter's validation number: " + validationKey);
                     } else {
                         System.out.println("Voter is unable to vote");
                     }
                 }
            }
        } catch (IOException e){
            e.getMessage();
        } finally {
            try{
                socket.close();
            } catch (IOException e){}
        }
    }

    /*Method updates voterList with all voterIDs already in the ballot
    * Thus ensuring voters dont get the chance to vote twice
    * Although it updates with every voter connection, bc I use a hashtable
    * the values never actually get duplicated*/
    public void updateVoterListWithBallots() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader("BallotList.txt"));
        String line = br.readLine();
        while (line != null){
            String[] temp = line.split("_");
            int validNum = Integer.parseInt(temp[1]);
            voterList.put(temp[0],validNum);
            line = br.readLine();
        }
    }

    public int checkUserValidation(String username){
        if (voterList.containsKey(username)){
            return 100000;
        } else {
            voterList.put(username, createValidNum());
            String voterMsg = username + "_" + voterList.get(username);
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
        System.out.println("\nConnecting to CTF...");

        try (Socket socket = new Socket("localhost", 1201)) {
            System.out.println("You are now connected to CTF on Port# " + socket.getPort() + "\n");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(voterMsg + "_CLA");
        } catch (IOException e){
            e.getMessage();
        }
    }
}