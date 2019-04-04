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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            System.out.println("Voter connected on: " +currentThread().getId());
            System.out.println("-----------------------------\n");

            String username;
            int validationKey;
            String receivedMsg;

            System.out.println("UPDATED VOTER LIST!!!");
            updateVoterListWithBallots();
            System.out.println(voterList);
            System.out.println("-----------------------------\n");

            while((receivedMsg = in.readLine()) != null){
                username = receivedMsg;
                System.out.println("Received: " + username);
                System.out.println("-----------------------------\n");
                validationKey = checkUserValidation(username);
                out.println(validationKey);
                System.out.println("Sent to voter: " + validationKey);
                System.out.println("-----------------------------\n");
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

    /* If user is valid, call getValidNum
     * Else, Create validNum, add to array list, and call sendToCTF */
    public int checkUserValidation(String username){
        if (voterList.containsKey(username)){
            return voterList.get(username);
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
        System.out.println("Connecting to CTF...");

        try (Socket socket = new Socket("localhost", 1201)) {
            System.out.println("You are now connected to CTF on Port# " + socket.getPort());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(voterMsg + "_CLA");
            //if I wanted to read anything from CTF:
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