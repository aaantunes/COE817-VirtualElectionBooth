package VirtualElectionBooth.CTF;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.security.*;
import javax.crypto.*;
import VirtualElectionBooth.Encryption.*;

public class CTF {

    /*Goals:
    * X Receive voterList from CLA
    * X Receive VoterMsg from Voter
    *   if voterMsg is in file:
    *       cannot vote, returns vote?
    *   else:
    *       add voterMsg to ballotList.txt
    *
    * X On receiving admin or exit voter:
    *    Tally up results from file
    *    display winner
    *
    * Voter voted verification method:
    *   send hashed version of file to Voter
    *   such that they can verify their vote counter
    *
    * X Add a method to tell voter that he cannot vote:
    *   currently, voter is still given the option to choose who to vote for
    *   although he doesnt actually vote. it would be nice to not give him the option
    * */
    JEncryptDES des = new JEncryptDES();
    static JEncryptRSA rsa = new JEncryptRSA();
    private SecretKey DESkey = null;
    private static KeyPair keyPair = rsa.buildKeyPair();
    private static PublicKey pubKey = keyPair.getPublic();
    private static PrivateKey privateKey = keyPair.getPrivate();

    public static void main(String[] args) {
        //Set up connection with voter
        try (ServerSocket serverSocket = new ServerSocket(1201)){
            System.out.println("CTFServer Starting...\nWaiting for connections...\n");

            while(true){
                new CTFServer(serverSocket.accept(), pubKey, privateKey).start();
                //no way to exit application bc of this while loop
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

class CTFServer extends Thread{
    private Socket socket;
    private PublicKey pubKey;
    private PrivateKey privateKey;
    static JEncryptDES des = new JEncryptDES();
    static JEncryptRSA rsa = new JEncryptRSA();

    //list containing all eligable voters(Who can still vote/ have not voted yet) received from CLA
    private static ArrayList<String> voterList = new ArrayList<>();

    public CTFServer(Socket socket, PublicKey pub, PrivateKey priv){
        this.socket = socket;
        pubKey = pub;
        privateKey = priv;
    }

    @Override
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            Transmitter tr = new Transmitter();
            Frame tFrame = new Frame();
            Frame rFrame = new Frame();

            tFrame.data = pubKey.getEncoded();
            os.writeObject(tFrame);
            os.reset();
            rFrame = (Frame) is.readObject();
            PublicKey CLAVoterPub = rFrame.getPublic();
            rFrame = (Frame) is.readObject();
            rFrame.data = rsa.decrypt(privateKey , rFrame.data);
            SecretKey desKey = rFrame.getDES();

            String voterID = "";
            String voterMessage = "";
            String receivedMsg;

            //while((receivedMsg = in.readLine()) != null){
            while((receivedMsg = tr.recieve(is, desKey)) != null){
                if (receivedMsg.contains("CLA")){
                    //received msg from CLA thread
                    String[] voterSplit = receivedMsg.split("_");
                    voterID = voterSplit[0] + "_" + voterSplit[1];

                    voterList.add(voterID);
                } else {
                    //receviced msg from Voter thread
                    System.out.println("--------------------------------------------");
                    voterMessage = receivedMsg;
                    System.out.println(receivedMsg);
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
        } catch (Exception e){
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
