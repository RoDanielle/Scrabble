package test;

import model.GuestModel;

public class GuestModelTest {
    public static void main(String[] args) {

        GuestModel guestModelTest = new GuestModel("TestGuest", "localHost" ,8081);

        //-----------------------startGuestGame function is activated automatically------------------------------
        //-----------------------------addTiles------------------------------

        String addedTiles = "A.B.C";
        guestModelTest.addTiles(addedTiles);
            if (guestModelTest.getTiles().size() != 3)
                System.out.println("Test 1 failed: Incorrect number of tiles");
            if (!(guestModelTest.getTiles().get(0).equals("A")))
                System.out.println("Test 2 failed: Incorrect number of tiles");
            if (!(guestModelTest.getTiles().get(1).equals("B")))
                System.out.println("Test 3 failed: Incorrect number of tiles");
            if (!(guestModelTest.getTiles().get(2).equals("C")))
                System.out.println("Test 4 failed: Incorrect number of tiles");

        //-----------------------------challengeTrue------------------------------

        String[] str = new String[9];
        str[0] = "3";
        str[1] = "true";
        str[2] = "10";
        str[3] = "A,1.B,2.C,3.D,4";
        str[4] = "TEST";
        str[5] = "HORN";
        str[6] = "1";
        str[7] = "1";
        str[8] = "vertical";

        int oldScore = guestModelTest.getScore();
        guestModelTest.challengeTrue(str);

        if (oldScore == guestModelTest.getScore())
            System.out.println("Test 5 failed");

        if (guestModelTest.getBoard()[1][1].equals("H"))
            System.out.println("Test 6 failed");

        if (guestModelTest.getBoard()[2][1].equals("H"))
            System.out.println("Test 7 failed");

        if (guestModelTest.getBoard()[3][1].equals("H"))
            System.out.println("Test 8 failed");

        if (guestModelTest.getBoard()[4][1].equals("H"))
            System.out.println("Test 9 failed");

        System.out.println("done");
    }
}