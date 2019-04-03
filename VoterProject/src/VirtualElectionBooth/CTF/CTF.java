package VirtualElectionBooth.CTF;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CTF {

    /*Goals:
    * Receive voterList from CLA
    * X Receive VoterMsg from Voter
    *   if voterMsg is in file:
    *       cannot vote, returns vote?
    *   else:
    *       add voterMsg to ballotList.txt
    *
    * On CLA closing connection:
    *    Tally up results from file
    *    display winner
    *
    * Voter voted verification method:
    *   send hashed version of file to Voter
    *   such that they can verify their vote counted
    *
    * */

    public static void main(String[] args) {
        //Set up connection with voter
        try (ServerSocket serverSocket = new ServerSocket(1201)){
            System.out.println("CTFServer Starting...\nWaiting for connections...");

            while(true){
                new CTFServer(serverSocket.accept()).start();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

class CTFServer extends Thread{
    private Socket socket;

    //list containing all eligable voters received from CLA
    private static ArrayList<String> voterList = new ArrayList<>();

    public CTFServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            System.out.println("Client connected on: " + currentThread().getName());

            String voteMsg = "DOESNT WORK";
            String receivedMsg;

            while((receivedMsg = in.readLine()) != null){
                if (receivedMsg.contains("CLA")){
                    System.out.println("RECEIVED CLA THREAD");
                    String[] voterInfo = receivedMsg.split("_");
                    System.out.println("Adding: " + receivedMsg + ", to voterList\n");
                    voterList.add(receivedMsg);
                }
                voteMsg = receivedMsg;
                System.out.println("Received: " + voteMsg + " from voter");
//                out.println(); //send something idk?
            }

            System.out.println(voterList);
            if(canVoterVote(voteMsg)){
                writeToFile(voteMsg);
            }

        } catch (IOException e){
            e.getMessage();
        } finally {
            try{
                socket.close();
            } catch (IOException e){}
        }
    }

    /*USE IF WANT TO DELETE FILE EVERYTIME
     *Or dont use so we have a larger voter list when demoing*/
    public void deleteFile(){
        File file = new File("BallotList.txt");
        file.delete();
    }

    public void writeToFile(String voteMsg){
        FileWriter fw = null;
        try{
            fw = new FileWriter("BallotList.txt", true);
            fw.write(voteMsg + "\n");
            fw.close();
        } catch (IOException e){
            e.getMessage();
        }
    }

    /*Checks if voterMsg sent by voter is in voterList (allowing them to vote)
    *   add voterMsg to Ballot.txt
    * else
    *   send out voter already voted (proof of vote?)
    */
    public boolean canVoterVote(String voterMsg){
        boolean canVote = false;

        return canVote;
    }

    /* If user is valid, call getValidNum
     * Else, Create validNum, add to array list, and call sendToCTF */
//    public int checkUserValidation(String username){
//        if (voterList.containsKey(username)){
//            return voterList.get(username);
//        } else {
//            voterList.put(username, createValidNum());
//            return voterList.get(username);
//        }
//    }
}
