package viewModel;

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
   private final ObservableList<String> tilesProperty;
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
                updateScoreFromModel(gameModel.getScore());
            }
            else if(arg.equals("name"))
            {
                updateNameFromModel(gameModel.getName());
            }
            else if(arg.equals("board"))
            {
                updateBoardFromModel(gameModel.getBoard());
            }
            else if(arg.equals("tiles"))
            {
                this.updateTilesFromModel(gameModel.getTiles());
            }
            else // message
            {
                message = gameModel.getMessage();
                if(message.contains("turn"))
                {
                    startUserQueryTurn();
                }
                else if(message.contains("challenge"))
                {
                    startUserChallengeTurn();
                }
                else {
                    updateMessageFromModel(arg.toString()); // messages to show on screen without getting input from user
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
    }

    public void startUserChallengeTurn() {
        // Perform any necessary actions to set up the user's turn
        isUserChallenge.set(true);
    }

    public void processQueryInput(String userInput) {
        if (isUserTurn.get()) {
            // Process the user's input
            // TODO maybe do validations with users tiles
            // ...
            isUserTurn.set(false); // Indicate the end of the user's turn
            gameModel.setUserQueryInput("userInput-word","userInput-row","userInput-col","userInput-vertical"); // TODO - put info accordingly
        }
    }
    public void processChallengeInput(String userInput) {
        if (isUserChallenge.get()) {
            // Process the user's input
            // ...
            isUserChallenge.set(false); // Indicate the end of the user's turn
            gameModel.setUserChallengeInput("userInput-c"); // TODO - put info accordingly
        }
    }

    // tiles update and get
    public void updateTilesFromModel(List<String> updatedTiles) {
        tilesProperty.setAll(updatedTiles);
    }

    public ObservableList<String> tilesProperty() {
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
        return nameProperty;
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
    }
}