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
    *   such that they can verify their vote counter
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

    //list containing all eligable voters(Who can still vote/ have not voted yet) received from CLA
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
            System.out.println("-----------------------------\n");

            String voterID = "";
            String voterMessage = "";
            String receivedMsg;

//            updateVoterListWithBallots(); //updates on start up?

            while((receivedMsg = in.readLine()) != null){
                if (receivedMsg.contains("CLA")){
                    System.out.println("RECEIVED CLA THREAD");
                    String[] voterSplit = receivedMsg.split("_");
                    voterID = voterSplit[0] + "_" + voterSplit[1];

                    System.out.println("Adding: " + voterID + ", to voterList\n");
                    voterList.add(voterID);
                } else {
                    System.out.println("RECEIVED VOTER THREAD");
                    voterMessage = receivedMsg;
                    System.out.println("Received: " + voterMessage + " from voter");
                    System.out.println("-----------------------------\n");

                    String[] voterSplit = voterMessage.split("_");
                    voterID = voterSplit[0] + "_" + voterSplit[1];

                    if(canVoterVote(voterID)){
                        writeToFile(voterMessage);
                        System.out.println("VoterList before remove" +voterList);
                        voterList.remove(voterID);
                        System.out.println("VoterList after remove" +voterList);
                    }
                }
//                out.println(); //If need to send something send here?
            }

        } catch (IOException e){
            e.getMessage();
        } finally {
            try{
                socket.close();
            } catch (IOException e){}
        }
    }

    /*Checks if voterMsg sent by voter is in voterList (allowing them to vote)
     *   add voterMsg to Ballot.txt
     * else
     *   send out voter already voted (proof of vote?)
     */
    public boolean canVoterVote(String voterMsg){
        boolean canVote = false;
        System.out.println("\ncanVoterVote's voterMsg is: " + voterMsg);
        if (voterList.contains(voterMsg)){
            System.out.println("VOTER CAN VOTE!!!\n");
            canVote = true;
        }
        return canVote;
    }

    public void writeToFile(String voteMsg) throws IOException{
        FileWriter fw = new FileWriter("BallotList.txt", true);
        fw.write(voteMsg + "\n");
        fw.close();
    }

    /*USE IF WANT TO DELETE FILE EVERYTIME
     *Or dont use so we have a larger voter list when demoing*/
    public void deleteFile(){
        File file = new File("BallotList.txt");
        file.delete();
    }
}
