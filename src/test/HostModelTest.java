package test;


import model.HostModel;

import java.util.ArrayList;

public class HostModelTest {

    public static void start() {
        HostModel hostModel = new HostModel();
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
