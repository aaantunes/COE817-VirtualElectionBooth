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
            while (true) {
                if ((validNum = in.readLine()) != null) {
                    System.out.println("Valid Num is: " + validNum);
                    break;
                }
            }
            connectToCTF(createVote(username,validNum));

        } catch (IOException e){
            e.getMessage();
        }
    }

    public static String createVote(String username, String validationKey){
        displayCandidates();
        Scanner scanner = new Scanner(System.in);
        int vote = scanner.nextInt();
        String voteMsg = username + "_" + validationKey+ "_" + vote;
        System.out.println("TESTING: " + voteMsg);
        return voteMsg;
    }

    public static void displayCandidates(){
        System.out.println("\nHi voter! The candidates running in this election are:\n" +
                "1. Andre Antunes\n" +
                "2. Ahmed Diab\n" +
                "To vote please enter the candidates respective value!");
    }


    public static void connectToCTF(String vote){
        System.out.println("Connecting to CTF...");

        try (Socket socket = new Socket("localhost", 1201)) {
            System.out.println("You are now connected to CTF on Port# " + socket.getPort());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

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
