package test;

import model.HostModel;
import model.Player;
import model.Tile;

import java.util.ArrayList;
import java.util.List;


public class HostModelTest {
    public static void main(String[] args) {
        String name = "Host|Test";
        Boolean isLocal = true;
        int numPlayers = 2;
        HostModel hostModelTest = new HostModel(name, isLocal, numPlayers);



        //-------------------------initialized properly-------------------------------------------
        // Test 1: Check if the host player is initialized properly
        //if (!(hostModel.hostPlayer != null && hostModel.hostPlayer.getName().equals("Host")))
            //System.out.println("Test 1 failed: Host player is not initialized correctly.");

        // Test 2: Check if the board is initialized properly
        if (!(hostModelTest.getBoard() != null && hostModelTest.getBoard().length == 15 && hostModelTest.getBoard()[0].length == 15))
            System.out.println("Test 2 failed: Board is not initialized correctly.");

        // Test 3: Check if the game is running initially
        if (!(hostModelTest.getGameRunning()))
            System.out.println("Test 3 failed: Game is not running initially.");

        // Test 4: Check if the bag object is initialized properly
        if (hostModelTest.getBag() == null)
            System.out.println("Test 4 failed: Bag object is not initialized correctly.");

        // Test 5: Check if the number of players is set correctly
        if (!(hostModelTest.getNumbOfPlayers() == 2))
            System.out.println("Test 5 failed: Number of players is not set correctly.");

        // Test 6: Check if the message is initially null
        if (!(hostModelTest.getMessage() == null))
            System.out.println("Test 6 failed: Message is not initially null.");

        // Test 7: Check if the players list is initialized properly
        if (!(hostModelTest.getPlayers() != null && hostModelTest.getPlayers().isEmpty()))
            System.out.println("Test 7 failed: Players list is not initialized correctly.");

        // Test 8: Check if the current player is set correctly
        if (!(hostModelTest.current_player != null && hostModelTest.current_player.equals(hostModelTest.hostPlayer)))
            System.out.println("Test 8 failed: Current player is not set correctly.");

        // Test 9: Check if the board is filled with underscores initially
        //boolean boardIsCorrect = true;
        //for (String[] row : hostModel.getBoard()) {
            //for (String cell : row) {
                //if (!cell.equals("_")) {
                    //boardIsCorrect = false;
                    //break;
                //}

        //if (!boardIsCorrect)
            //System.out.println("Test 9 failed: Board is not filled with underscores initially");


        //-----------------------GameManagement function is activated automatically------------------------------

        //בדיקה עבור תחילת המשחק כאשר ההוסט הוא השחקן הראשון במבנה נתונים
        //השאלה כיצד ניתן לדעת אם בזמן בדיקה כרגע אכן מדובר על תחילת המשחקן
        //אני חושבת שבגלל זה זה לא עובר את הבדיקה
        //if (isLocal) {
        //    if (!(hostModel.current_player == hostModel.hostPlayer))
        //        System.out.println("Test 10 failed");
        //}


        //-----------------------setLocalPlayers function is activated automatically------------------------------
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setName("Host");
        player2.setName("Test");
        List<Player> playersTest = new ArrayList<>();
        playersTest.add(player1);
        playersTest.add(player2);
        Tile.Bag bagTest = Tile.Bag.getBag();

        if (!(playersTest.get(0).getName().equals("Host")))
            System.out.println("Test 11 failed: The player is not initialized correctly.");
        if (!(playersTest.get(1).getName().equals("Test")))
            System.out.println("Test 11 failed: The player is not initialized correctly."); //NullPointerException

        //-----------------------startGame_local function is activated automatically------------------------------
        //-----------------------playerTurn function is activated automatically------------------------------
        //-----------------------giveTiles function is activated automatically------------------------------

        for (int i=0; i<playersTest.size(); i++) {
            hostModelTest.giveTiles(playersTest.get(i));
            if (playersTest.get(i).getNumOfTiles() != 7)
                System.out.println("Test 12 failed");
        }
        //-----------------------addScore------------------------------
        int oldScore1 = player1.getScore();
        int oldScore2 = player2.getScore();

        player1.addScore(10);
        if (!(oldScore1+10 == player1.getScore()))
            System.out.println("Test 13 failed");

        player2.addScore(15);
        if (!(oldScore1+15 == player2.getScore()))
            System.out.println("Test 13 failed");


        //-----------------------decreaseScore------------------------------
        int oldScore3 = player1.getScore();
        int oldScore4 = player2.getScore();

        player1.decreaseScore(10);
        if (!(oldScore3-10 == player1.getScore()))
            System.out.println("Test 14 failed");

        player2.decreaseScore(3);
        if (!(oldScore4-3 == player1.getScore()))
            System.out.println("Test 14 failed");

        //-----------------------TryPutWordInBoard------------------------------

        //-----------------------fill_spaces------------------------------
        //hostModelTest.fill_spaces("T_st");

        //-----------------------setUserQueryInput------------------------------

        //-----------------------setUserChallengeInput------------------------------

        //-----------------------returnTiles------------------------------

        //-----------------------passesCountLocal------------------------------

        //-----------------------stopLocalGame------------------------------
        int maxScoreTest = 0;
        String winnerNameTest = null;
        for (int i=0; i<playersTest.size(); i++) {
            if (playersTest.get(i).getScore() > maxScoreTest) {
                maxScoreTest = playersTest.get(i).getScore();
                winnerNameTest = playersTest.get(i).getName();
            }
        }

        hostModelTest.setPlayers(playersTest);
        hostModelTest.stopLocalGame();
        if (!(hostModelTest.getWinner().getName().equals(winnerNameTest)))
            System.out.println("Test 15 failed");
        if (!(hostModelTest.getWinner().getScore() == maxScoreTest))
            System.out.println("Test 15 failed");

        System.out.println("done");
    }
}