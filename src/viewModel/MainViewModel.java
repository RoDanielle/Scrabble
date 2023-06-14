package viewModel;

import javafx.application.Platform;
import javafx.beans.property.*;
import model.GameModel;
import model.GuestModel;
import model.HostModel;

import java.util.Observable;
import java.util.Observer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import java.util.List;
import javafx.beans.property.StringProperty;



public class MainViewModel implements Observer {

    private String message;
   //private final ObservableList<String> tilesProperty;
    private final List<StringProperty> tilesProperty;
    private final StringProperty nameProperty;
    private final StringProperty msgProperty;
    private final IntegerProperty scoreProperty;
    private final ObjectProperty<StringProperty[][]> boardProperty;
    private final GameModel gameModel;
    private final BooleanProperty isUserTurn;
    private final BooleanProperty isUserChallenge;
    public MainViewModel(String name, String ip, String port, boolean isHost, boolean isLocal, int numOfPlayers) {
        if(isHost)
        {
            this.gameModel = new HostModel(name, isLocal, numOfPlayers);
            this.gameModel.addObserver(this);
        }
        else
        {
            this.gameModel = new GuestModel(name,ip,Integer.parseInt(port));
            this.gameModel.addObserver(this);
        }


        // tiles
        this.tilesProperty = FXCollections.observableArrayList();
        // board
        this.boardProperty = new SimpleObjectProperty<>();
        // name
        this.nameProperty = new SimpleStringProperty();
        // score
        this.scoreProperty = new SimpleIntegerProperty();
        // message
        this.msgProperty = new SimpleStringProperty();

        this.isUserTurn = new SimpleBooleanProperty(false);
        this.isUserChallenge = new SimpleBooleanProperty(false);
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel)
        {
            if(arg.equals("score"))
            {
                Platform.runLater(() -> {
                    updateScoreFromModel(gameModel.getScore());
                });
            }
            else if(arg.equals("name"))
            {
                Platform.runLater(() -> {
                    updateNameFromModel(gameModel.getName());
                });
            }
            else if(arg.equals("board"))
            {
                updateBoardFromModel(gameModel.getBoard());
            }
            else if(arg.equals("tiles"))
            {
                Platform.runLater(() -> {
                    this.updateTilesFromModel(gameModel.getTiles());
                });
            }
            else // message
            {
                message = gameModel.getMessage();
                if(message.contains("turn") && !message.contains("over") && !message.contains("wait"))
                {
                    Platform.runLater(() -> {
                        // Update the bound property here
                        updateMessageFromModel(message); // messages to show on screen without getting input from user
                    });
                    startUserQueryTurn();
                }
                else if(message.contains("challenge") && !message.contains("failed"))
                {
                    startUserChallengeTurn();
                }
                else {
                    //updateMessageFromModel(message); // messages to show on screen without getting input from user
                    Platform.runLater(() -> {
                        updateMessageFromModel(message); // messages to show on screen without getting input from user
                    });
                }
            }
        }
    }

    public BooleanProperty isUserTurnProperty() {
        return isUserTurn;
    }

    public BooleanProperty isUserchallengeProperty() {
        return isUserChallenge;
    }
    public void startUserQueryTurn() {
        // Perform any necessary actions to set up the user's turn
        isUserTurn.set(true);
        System.out.println("set user turn to true in viewmodel");
    }

    public void startUserChallengeTurn() {
        // Perform any necessary actions to set up the user's turn
        isUserChallenge.set(true);
    }

    public void processQueryInput(String userInput) {  // TODO - should be: word|row|col|vertical from View
        System.out.println("entered query pros");
        // TODO  - maybe do validations with users tiles
        // TODO - make all word letters to Uppercase before moving to model
        if (isUserTurn.get()) {
            // Process the user's input
            String[] request = userInput.split("[|]");
            if(request[0].equals("xxx"))
            {
                gameModel.setUserQueryInput(request[0],"null","null","null");
            }
            else
            {
                gameModel.setUserQueryInput(request[0],request[1],request[2],request[3]);
            }
        }
        isUserTurn.set(false); // Indicate the end of the user's turn
    }
    public void processChallengeInput(String userInput) {
        System.out.println("entered challenge pros");
        if (isUserChallenge.get()) {
            if(userInput.equals("yes"))
            {
                gameModel.setUserChallengeInput("C");
            }
            else {
                gameModel.setUserChallengeInput("xxx");
            }
        }
        isUserChallenge.set(false); // Indicate the end of the user's turn
    }

    // tiles update and get
    public void updateTilesFromModel(List<String> updatedTiles) {
        tilesProperty.clear();
        for(String s : updatedTiles)
        {
            StringProperty sp = new SimpleStringProperty();
            sp.set(s);
            tilesProperty.add(sp);
        }
    }

    public List<StringProperty> tilesProperty() {
        return tilesProperty;
    }

    // score update and get
    public void updateScoreFromModel(int score) {
       scoreProperty.set(score);
    }

    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    // name update and get
    public void updateNameFromModel(String name)
    {
        nameProperty.set(name);
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    // message update and get
    public void updateMessageFromModel(String msg)
    {
        msgProperty.set(msg);
    }
    public StringProperty MsgProperty() {
        return msgProperty;
    }

    // board update and get
    public ObjectProperty<StringProperty[][]> boardProperty() {
        return boardProperty;
    }

    public void updateBoardFromModel(String[][] updatedBoard) {
        StringProperty[][] boardWrapper = new StringProperty[updatedBoard.length][updatedBoard[0].length];
        for (int i = 0; i < updatedBoard.length; i++) {

            for (int j = 0; j < updatedBoard[i].length; j++) {
                boardWrapper[i][j] = new SimpleStringProperty(updatedBoard[i][j]);
            }
        }
        boardProperty.set(boardWrapper);
        //printmatrix();
    }

    public void printmatrix() // will move to view later on
    {
        System.out.println("the board from view model is: ");
        System.out.print("  ");
        for(int k = 0; k < 15; k++)
        {
            System.out.print(" " + k + " ");
        }
        System.out.println("");
        for(int i = 0; i < 15; i++)
        {
            System.out.print( i + " " );
            for(int j = 0; j < 15; j++)
            {
                if(this.boardProperty.get()[i][j] != null)
                {
                    if(j < 11)
                    {
                        System.out.print(" " + this.boardProperty.get()[i][j]+ " ");
                    }
                    else
                    {
                        System.out.print("  " + boardProperty.get()[i][j] + " ");
                    }

                }
                else {
                    if(j < 11)
                    {
                        System.out.print(" _ ");
                    }
                    else
                    {
                        System.out.print("  _ ");
                    }
                }
            }
            System.out.println("");
        }
    }
}