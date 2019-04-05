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
                //no way to exit application bc of this while loop
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
                    //received msg from CLA thread
                    String[] voterSplit = receivedMsg.split("_");
                    voterID = voterSplit[0] + "_" + voterSplit[1];

                    voterList.add(voterID);
                } else {
                    //receviced msg from Voter thread
                    System.out.println("--------------------------------------------");
                    voterMessage = receivedMsg;

                    if (voterMessage.toUpperCase().equals("EXIT")){
                        tallyVotes();
                        break;
                    } else {
                        String[] voterSplit = voterMessage.split("_");
                        voterID = voterSplit[0] + "_" + voterSplit[1];

                        if(canVoterVote(voterID)){
                            System.out.println(voterSplit[0] + " has voted!");
                            writeToFile(voterMessage);
                            voterList.remove(voterID);
                        }
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

    public boolean canVoterVote(String voterMsg){
        boolean canVote = false;
        if (voterList.contains(voterMsg)){
            canVote = true;
        }
        return canVote;
    }

    public void writeToFile(String voteMsg) throws IOException{
        FileWriter fw = new FileWriter("BallotList.txt", true);
        fw.write(voteMsg + "\n");
        fw.close();
    }

    /*Tally votes after receiving a message to talley votes
    * Could do either admin voter or tally votes voter*/
    public void tallyVotes() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader("BallotList.txt"));
        String line = br.readLine();
        int vote1 = 0, vote2 = 0;
        while (line != null){
            String[] temp = line.split("_");
            int vote = Integer.parseInt(temp[2]);
            if (vote == 1){
                vote1++;
            } else if(vote == 2){
                vote2++;
            }
            line = br.readLine();
        }
        if (vote1 > vote2){
            System.out.println("Andre Antunes wins the election!");
        } else if (vote1 < vote2){
            System.out.println("Ahmed Diab wins the election!");
        } else if (vote1 == vote2){
            System.out.println("There was a tie!");
        } else {
            System.out.println("Something went wrong");
        }
    }
}
