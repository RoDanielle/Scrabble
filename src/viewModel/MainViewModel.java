package viewModel;

import javafx.beans.property.*;
import model.GameModel;
import model.GuestModel;
import model.HostModel;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class MainViewModel implements Observer {

     String message;
    private final ObjectProperty<List> tiles;

    //private final ListProperty<String> tiles;
    private final StringProperty nameProperty;
    private final IntegerProperty scoreProperty;
    //private final StringProperty[][] boardProperty;

    private final ObjectProperty<String[][]> boardProperty;
    private final GameModel gameModel;

    public MainViewModel(String name, String ip, String port, boolean isHost, boolean isLocal, int numOfPlayers) {
        tiles = new SimpleObjectProperty<List>();
        nameProperty = new SimpleStringProperty();
        scoreProperty = new SimpleIntegerProperty();
        boardProperty = new SimpleObjectProperty<String[][]>();
        if(isHost)
            gameModel = new HostModel(name, isLocal, numOfPlayers,this);
        else
            gameModel = new GuestModel(name,ip,Integer.parseInt(port),this);
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel)
        {
            if(arg.equals("score"))
            {
                scoreProperty.set(gameModel.getScore());
            }
            else if(arg.equals("name"))
            {
                nameProperty.set(gameModel.getName());
            }
            else if(arg.equals("board"))
            {
                boardProperty.set(gameModel.getBoard());
            }
            else if(arg.equals("tiles"))
            {
                ////tiles.set(GameModel);
            }
            else // message
            {
                message = gameModel.getMessage();

                // here if there are requests for an input we will ask the view
            }
        }
    }


    // Getter for the name property
    public StringProperty nameProperty() {
        return nameProperty;
    }

    // Getter for the name value
    public String getName() {
        return nameProperty.get();
    }

    // Setter for the name value
    public void setName(String name) {
        nameProperty.set(name);
    }


    // Getter for the score property
    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    // Getter for the score value
    public int getScore() {
        return scoreProperty.get();
    }

    // Setter for the score value
    public void setScore(int score) {
        scoreProperty.set(score);
    }


    public ObjectProperty<String[][]> boardProperty() {
        return boardProperty;
    }

    public void setBoard(String[][] board) {
        boardProperty.set(board);
    }

    public String[][] getBoard() {
        return boardProperty.get();
    }
}