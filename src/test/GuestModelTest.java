package test;


import model.GuestModel;

public class GuestModelTest {
    public static void main(String[] args) {

        GuestModel guestModelTest = new GuestModel("TestGuest", "localHost" ,8081);
        //לבדוק שהשם שהכנסנו במופע מופיע ברשימת השחקנים של המשחק

        //-----------------------------addTiles------------------------------
        String addedTiles = "A.B.C";
        guestModelTest.addTiles(addedTiles);
        if (guestModelTest.getTiles().size() != 3)
            System.out.println("Test 1 failed: Incorrect number of tiles");
        if (!(guestModelTest.getTiles().get(0).equals("A")))
            System.out.println("Test 1 failed: Incorrect number of tiles");
        if (!(guestModelTest.getTiles().get(1).equals("B")))
            System.out.println("Test 1 failed: Incorrect number of tiles");
        if (!(guestModelTest.getTiles().get(2).equals("C")))
            System.out.println("Test 1 failed: Incorrect number of tiles");

        //-----------------------------challengeResponse------------------------------

        // Simulate a correct challenge response
        String[] correctResponse = { "3", "true", "name" };
        guestModelTest.challengeResponse(correctResponse);
        // Check if the appropriate method was called or message was set correctly


        // Simulate an incorrect challenge response
        String[] incorrectResponse = { "3", "false", "name" };
        guestModelTest.challengeResponse(incorrectResponse);
        // Check if the appropriate method was called or message was set correctly


        // Sleep to allow for the delay in the method
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Check if the message was set correctly after the delay


        //-----------------------------wordEnteredByOtherUser------------------------------

        //-----------------------------wordEnteredByMe------------------------------

        // Simulate a successful word placement
        String[] successfulPlacement = { "2", "true", "10", "A.B.C", "2", "0", "1", "2", "3" };
        guestModelTest.wordEnteredByMe(successfulPlacement);

        // Check if the appropriate methods were called or messages were set correctly


        // Simulate an unsuccessful word placement
        String[] unsuccessfulPlacement = { "2", "true", "0", "A.B.C", "2", "0", "1", "2", "3" };
        guestModelTest.wordEnteredByMe(unsuccessfulPlacement);

        // Check if the appropriate methods were called or messages were set correctly

        //-----------------------------challengeTrue------------------------------

    }
}