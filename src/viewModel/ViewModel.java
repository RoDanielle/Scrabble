package viewModel;

import model.GameModel;
import model.GuestModel;
import model.HostModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/*
public class ViewModel implements Observer {

    String message;
    // game player info
    String[][] board;
    int score;
    String playerName;
    List<String> tiles;
    GameModel model;

    public ViewModel(String name, String ip, String port, boolean isHost, boolean isLocal, int numOfPlayers)
    {
        playerName = name;
        board =  new String[15][15];
        score = 0;
        tiles = new ArrayList<>();
        message = null;

        if(isHost)
            model = new HostModel(name, isLocal, numOfPlayers,this);
        else
            model = new GuestModel(name,ip,Integer.parseInt(port),this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel)
        {
            if(arg.equals("score"))
            {
                score = model.getScore();
            }
            else if(arg.equals("name"))
            {
                playerName = model.getName();
            }
            else if(arg.equals("board"))
            {
                board = model.getBoard();
            }
            else if(arg.equals("tiles"))
            {
                tiles = model.getTiles();
            }
            else // message
            {
                message = model.getMessage();

                // here if there are requests for an input we will ask the view
            }
        }
    }
}

 */
