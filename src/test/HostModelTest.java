package test;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class HostModelTest {

    public static void main(String[] args) {
        String name = "Host|Test";
        Boolean isLocal = true;
        int numPlayers = 2;
        HostModel hostModelTest = new HostModel(name, isLocal, numPlayers);
        int oldScore = 0;


        //-------------------------initialized properly-------------------------------------------

        // Test 1: Check if the board is initialized properly
        if (!(hostModelTest.getBoard() != null && hostModelTest.getBoard().length == 15 && hostModelTest.getBoard()[0].length == 15))
            System.out.println("Test 1 failed: Board is not initialized correctly.");

        // Test 2: Check if the game is running initially
        if (!(hostModelTest.getGameRunning()))
            System.out.println("Test 2 failed: Game is not running initially.");

        // Test 3: Check if the bag object is initialized properly
        if (hostModelTest.getBag() == null)
            System.out.println("Test 3 failed: Bag object is not initialized correctly.");

        // Test 4: Check if the number of players is set correctly
        if (!(hostModelTest.getNumbOfPlayers() == 2))
            System.out.println("Test 4 failed: Number of players is not set correctly.");

        // Test 5: Check if the message is initially null
        if (!(hostModelTest.getMessage() == null))
            System.out.println("Test 5 failed: Message is not initially null.");

        // Test 6: Check if the players list is initialized properly
        if (!(hostModelTest.getPlayers().isEmpty()))
            System.out.println("Test 6 failed: Players list is not initialized correctly.");

        //-----------------------GameManagement function is activated automatically------------------------------
        //-----------------------setLocalPlayers function is activated automatically------------------------------

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!(hostModelTest.getPlayers().get(0).getName().equals("Host") || hostModelTest.getPlayers().get(0).getName().equals("Test")))
            System.out.println("Test 7 failed: The player is not initialized correctly.");
        if (!(hostModelTest.getPlayers().get(1).getName().equals("Test") || hostModelTest.getPlayers().get(1).getName().equals("Host")))
            System.out.println("Test 8 failed: The player is not initialized correctly.");

        //-----------------------startGame_local function is activated automatically------------------------------
        //-----------------------playerTurn function is activated automatically------------------------------
        //-----------------------giveTiles function is activated automatically------------------------------
        for (int i=0; i< hostModelTest.getNumbOfPlayers(); i++) {
            List <Player> l = hostModelTest.getPlayers();
            Player p = l.get(i);
            hostModelTest.giveTiles(p);
            int numOfTiles = hostModelTest.getPlayers().get(i).getTiles().size();
            if (numOfTiles != 7)
                System.out.println("Test 9 failed: Initialize tiles at game start");
        }

        //-----------------------addScore------------------------------
        oldScore = hostModelTest.getPlayers().get(0).getScore();
        hostModelTest.getPlayers().get(0).addScore(10);
        if (!(oldScore+10 == hostModelTest.getPlayers().get(0).getScore()))
            System.out.println("Test 10 failed: Score update");

        oldScore = hostModelTest.getPlayers().get(1).getScore();
        hostModelTest.getPlayers().get(1).addScore(15);
        if (!(oldScore+15 == hostModelTest.getPlayers().get(1).getScore()))
            System.out.println("Test 11 failed: Score update");

        //-----------------------decreaseScore------------------------------
        oldScore = hostModelTest.getPlayers().get(0).getScore();
        hostModelTest.getPlayers().get(0).decreaseScore(10);
        if (!(oldScore-10 == hostModelTest.getPlayers().get(0).getScore()))
            System.out.println("Test 12 failed: Score update");

        oldScore = hostModelTest.getPlayers().get(1).getScore();
        hostModelTest.getPlayers().get(1).decreaseScore(3);
        if (!(oldScore-3 == hostModelTest.getPlayers().get(1).getScore()))
            System.out.println("Test 13 failed: Score update");

        //-----------------------TryPutWordInBoard------------------------------
        // Simulate a successful word placement
        Player player1 = new Player();
        player1.setName("Host");
        oldScore = player1.getScore();

        Tile t1 = hostModelTest.getBag().getTile('H');
        Tile t2 = hostModelTest.getBag().getTile('O');
        Tile t3 = hostModelTest.getBag().getTile('R');
        Tile t4 = hostModelTest.getBag().getTile('N');

        List<Tile> tilesTest = new ArrayList<>();
        tilesTest.add(t1);
        tilesTest.add(t2);
        tilesTest.add(t3);
        tilesTest.add(t4);

        player1.setTiles(tilesTest);

        hostModelTest.current_player = player1;
        String[] successfulPlacement = { "C", "HORN", "7", "5", "false" };
        hostModelTest.TryPutWordInBoard(successfulPlacement[0], successfulPlacement[1], successfulPlacement[2], successfulPlacement[3], successfulPlacement[4]);

        if (player1.getScore() == oldScore)
            System.out.println("Test 14 failed");


        //-----------------------stopLocalGame------------------------------
        int maxScoreTest = 0;
        String winnerNameTest = null;
        for (int i=0; i<hostModelTest.getNumbOfPlayers(); i++) {
            if (hostModelTest.getPlayers().get(i).getScore() > maxScoreTest) {
                maxScoreTest = hostModelTest.getPlayers().get(i).getScore();
                winnerNameTest = hostModelTest.getPlayers().get(i).getName();
            }
        }
        hostModelTest.stopLocalGame();
        if (!(hostModelTest.getWinner().getName().equals(winnerNameTest)))
            System.out.println("Test 15 failed");
        if (!(hostModelTest.getWinner().getScore() == maxScoreTest))
            System.out.println("Test 16 failed");

        System.out.println("done");
    }
}