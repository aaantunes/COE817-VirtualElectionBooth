package VirtualElectionBooth.Voter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Voter {

    String username;
    int validationKey;

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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String username;
            String validNum;

            System.out.println("Please enter a username: ");
            username = scanner.nextLine();
            out.println(username);
            if (!username.toUpperCase().equals("EXIT")){
                while (true) {
                    if ((validNum = in.readLine()) != null) {
                        if (!validNum.equals("100000")){
                            System.out.println("Voters Validation Number is: " + validNum);
                            connectToCTF(createVote(username,validNum));
                            break;
                        } else {
                            System.out.println("Sorry, you already voted");
                            //this is where we could show proof they voted (decrypt "BallotList.txt"?
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
//            connectToCTF(createVote(username,validNum));

        } catch (IOException e){
            e.getMessage();
        }
    }

    public static String createVote(String username, String validationKey){
        String voteMsg = "";
        if (!username.toUpperCase().equals("EXIT")){
            displayCandidates();
            Scanner scanner = new Scanner(System.in);
            int vote = scanner.nextInt();
            if (vote < 1 || vote > 2){
                System.out.println("Please enter an optional value");
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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Sending vote: " + vote);
            out.println(vote);
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

    public static void main(String[] args) {
        connectToCLA();
    }
}
