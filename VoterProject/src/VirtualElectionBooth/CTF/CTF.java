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
    * On receiving admin or exit voter:
    *    Tally up results from file
    *    display winner
    *
    * Voter voted verification method:
    *   send hashed version of file to Voter
    *   such that they can verify their vote counter
    *
    * Add a method to tell voter that he cannot vote:
    *   currently, voter is still given the option to choose who to vote for
    *   although he doesnt actually vote. it would be nice to not give him the option
    * */

    public static void main(String[] args) {
        //Set up connection with voter
        try (ServerSocket serverSocket = new ServerSocket(1201)){
            System.out.println("CTFServer Starting...\nWaiting for connections...\n");

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

            String voterID = "";
            String voterMessage = "";
            String receivedMsg;

            while((receivedMsg = in.readLine()) != null){
                if (receivedMsg.contains("CLA")){
                    String[] voterSplit = receivedMsg.split("_");
                    voterID = voterSplit[0] + "_" + voterSplit[1];

//                    System.out.println("Adding: " + voterID + ", to voterList");
                    voterList.add(voterID);
                } else {
                    System.out.println("--------------------------------------------");
                    voterMessage = receivedMsg;
//                    System.out.println("Received: " + voterMessage + " from voter");

                    String[] voterSplit = voterMessage.split("_");
                    voterID = voterSplit[0] + "_" + voterSplit[1];

                    if(canVoterVote(voterID)){
                        writeToFile(voterMessage);
                        voterList.remove(voterID);
                    } else {
                        //Send voter cannot vote to voter
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
        if (voterList.contains(voterMsg)){
            System.out.println("This voter is eligable to vote\n");
            canVote = true;
        } else {
            System.out.println("This voter is not eligable to vote\n");
        }
        return canVote;
    }

    public void writeToFile(String voteMsg) throws IOException{
        FileWriter fw = new FileWriter("BallotList.txt", true);
        fw.write(voteMsg + "\n");
        fw.close();
    }

    /*If ever need to delete File*/
    public void deleteFile(){
        File file = new File("BallotList.txt");
        file.delete();
    }
}
