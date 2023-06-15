package viewModel;

import javafx.application.Platform;
import javafx.beans.property.*;
import model.GameModel;
import model.GuestModel;
import model.HostModel;

import java.util.Observable;
import java.util.Observer;

import javafx.collections.FXCollections;
import java.util.List;
import javafx.beans.property.StringProperty;



public class MainViewModel implements Observer {

    private String message;
    private final List<StringProperty> tilesProperty;
    private final StringProperty nameProperty;
    private final StringProperty msgProperty;
    private final IntegerProperty scoreProperty;
    private final StringProperty[][] boardProperty;
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
        this.boardProperty = new StringProperty[15][15];
        // name
        this.nameProperty = new SimpleStringProperty();
        // score
        this.scoreProperty = new SimpleIntegerProperty();
        // message
        this.msgProperty = new SimpleStringProperty();

        this.isUserTurn = new SimpleBooleanProperty(false);
        this.isUserChallenge = new SimpleBooleanProperty(false);

        initializeTiles();
        initializeBoard();
    }

    private void initializeTiles()
    {
        for(int i = 0; i < 7; i++)
        {
            StringProperty strprop = new SimpleStringProperty();
            tilesProperty.add(strprop);
        }
    }

    private void initializeBoard()
    {
        for(int i = 0; i < 15; i++)
        {
            for(int j = 0; j < 15; j++)
            {
                StringProperty strprop = new SimpleStringProperty("      ");
                boardProperty[i][j] = strprop;
            }
        }
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
                Platform.runLater(() -> {
                    updateBoardFromModel(gameModel.getBoard());
                });
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
                        updateMessageFromModel(message); // messages to show on screen without getting input from user
                    });
                    startUserQueryTurn();
                }
                else if(message.contains("challenge") && !message.contains("failed"))
                {
                    startUserChallengeTurn();
                }
                else {
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
        isUserTurn.set(true);
    }

    public void startUserChallengeTurn() {
        isUserChallenge.set(true);
    }

    public void processQueryInput(String userInput) {  //  word|row|col|vertical from View
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
        int i = 0;
        for(String s : updatedTiles)
        {
            this.tilesProperty.get(i).setValue(s);
            i++;
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
    public StringProperty[][] boardProperty() {
        return boardProperty;
    }

    public void updateBoardFromModel(String[][] updatedBoard) {
        for(int i = 0; i < 15; i++)
        {
            for(int j = 0; j < 15; j++)
            {
                if(updatedBoard[i][j] != null) {
                    boardProperty[i][j].setValue(updatedBoard[i][j]);
                }
            }
        }
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
                if(this.boardProperty[i][j] != null)
                {
                    if(j < 11)
                    {
                        System.out.print(" " + this.boardProperty[i][j]+ " ");
                    }
                    else
                    {
                        System.out.print("  " + boardProperty[i][j] + " ");
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