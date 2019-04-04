package VirtualElectionBooth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/*Class is solely for testing shit I dont know how to do yet*/
public class main {

    private static Hashtable<String, Integer> voterList = new Hashtable<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("BallotList.txt"));
        String line = br.readLine();
        while (line != null){
            String[] temp = line.split("_");
            int validNum = Integer.parseInt(temp[1]);
            System.out.println("Valid num: " + validNum);
            voterList.put(temp[0],validNum);
            line = br.readLine();
        }

        System.out.println(voterList);
    }
}
