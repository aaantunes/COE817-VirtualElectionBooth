package VirtualElectionBooth;

public class main {
    public static void main(String[] args) {
        String temp = "Andre_4577_CLA";

        String[] voterInfo = temp.split("_");

        String voteMsg = "";
        for (int i = 0; i < voterInfo.length - 1; i++){
            System.out.println(voterInfo[i]);
            voteMsg += voterInfo[i] + "_";
        }

        System.out.println("\n"+voteMsg);
    }
}
