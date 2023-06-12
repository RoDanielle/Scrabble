package test;

import model.HostModel;
import model.Player;
import model.Tile;
import model.Word;

/*
public class HostModelTest {

    public static void start() {
        String name = "Test Host";
        Boolean isLocal = true;
        int numPlayers = 2;
        HostModel hostModel = new HostModel(name, isLocal, numPlayers);



        if (hostModel.set_num_players("8"))
            System.out.println("Your game exceeds the number of players that can be supported");

        if (hostModel.set_num_players("10"))
            System.out.println("Your game exceeds the number of players that can be supported");

        if (hostModel.set_num_players("-5"))
            System.out.println("Your game exceeds the number of players that can be supported");


        ArrayList<String> arrayNames = new ArrayList<>();
        arrayNames.add("shiraz");
        arrayNames.add("shahar");
        arrayNames.add("danielle");
        hostModel.create_players(arrayNames);
        if (hostModel.getPlayers().size() != hostModel.getNumbOfPlayers())
            System.out.println("You have not created the correct number of players");

        hostModel.order_players_turns(hostModel.getPlayers());
        if (hostModel.getPlayers().get(0).getTiles().get(0).letter > hostModel.getPlayers().get(1).getTiles().get(0).letter)
            System.out.println("The order of players is incorrect");

        for (int i = 0; i<hostModel.getNumbOfPlayers(); i++)
            hostModel.tile_completion(hostModel.getPlayers().get(i));

        if(hostModel.getPlayers().get(2).getTiles().size() != 7)
            System.out.println("The completion of the tiles is incorrect");

        if(hostModel.getPlayers().get(1).getTiles().size() != 7)
            System.out.println("The completion of the tiles is incorrect");


    }
}

 */

public class HostModelTest {
    public static void main(String[] args) {
        String name = "Host";
        Boolean isLocal = true;
        int numPlayers = 2;
        HostModel hostModel = new HostModel(name, isLocal, numPlayers);
        hostModel.GameManagement(isLocal, name);


        // Test 1: Check if the host player is initialized properly
        if (!(hostModel.hostPlayer != null && hostModel.hostPlayer.getName().equals("Host")))
            System.out.println("Test 1 failed: Host player is not initialized correctly.");

        // Test 2: Check if the board is initialized properly
        if (!(hostModel.getBoard() != null && hostModel.getBoard().length == 15 && hostModel.getBoard()[0].length == 15))
            System.out.println("Test 2 failed: Board is not initialized correctly.");

        // Test 3: Check if the game is running initially
        if (!(hostModel.gameRunning))
            System.out.println("Test 3 failed: Game is not running initially.");

        // Test 4: Check if the bag object is initialized properly
        if (hostModel.getBag() == null)
            System.out.println("Test 4 failed: Bag object is not initialized correctly.");

        // Test 5: Check if the number of players is set correctly
        if (!(hostModel.numbOfPlayers == 2))
            System.out.println("Test 5 failed: Number of players is not set correctly.");

        // Test 6: Check if the message is initially null
        if (!(hostModel.getMessage() == null))
            System.out.println("Test 6 failed: Message is not initially null.");

        // Test 7: Check if the players list is initialized properly
        if (!(hostModel.players != null && hostModel.players.isEmpty()))
            System.out.println("Test 7 failed: Players list is not initialized correctly.");

        // Test 8: Check if the current player is set correctly
        if (!(hostModel.current_player != null && hostModel.current_player.equals(hostModel.hostPlayer)))
            System.out.println("Test 8 failed: Current player is not set correctly.");

        // Test 9: Check if the board is filled with underscores initially
        boolean boardIsCorrect = true;
        for (String[] row : hostModel.getBoard()) {
            for (String cell : row) {
                if (!cell.equals("_")) {
                    boardIsCorrect = false;
                    break;
                }
            }
        }

        if (!boardIsCorrect)
            System.out.println("Test 9 failed: Board is not filled with underscores initially");
    }
}

