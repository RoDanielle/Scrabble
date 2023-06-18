package viewModel;

import javafx.application.Platform;
import javafx.beans.property.*;
import model.GameModel;
import model.GuestModel;
import model.HostModel;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.collections.FXCollections;
import java.util.List;
import javafx.beans.property.StringProperty;

import static java.lang.String.valueOf;


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
    private final boolean isLocal;
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

        this.isLocal = isLocal;

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
                else if(message.contains("challenge"))
                {
                    Platform.runLater(() -> {
                        updateMessageFromModel(message); // messages to show on screen without getting input from user
                    });
                    if(!message.contains("failed")) {
                        startUserChallengeTurn();
                    }
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
        if (isUserTurn.get()) {
            // Process the user's input
            String[] request = userInput.split("[|]");
            if(request[0].equals("xxx"))
            {
                gameModel.setUserQueryInput(request[0],"null","null","null");
            }
            else
            {
                String upperCase = request[0].toUpperCase();

                int charFound = 0;
                List tilesStrs = new ArrayList<String>();
                for(StringProperty str : this.tilesProperty)
                {
                    String s = str.getValue();
                    tilesStrs.add(s);
                }

                for(int i = 0; i < upperCase.length(); i++)
                {
                    if(upperCase.charAt(i) != '_')
                    {
                        for(int j = 0; j < tilesStrs.size(); j++) {
                            if(tilesStrs.get(j).toString().contains(valueOf(upperCase.charAt(i)))){
                                tilesStrs.remove(j);
                                charFound++;
                                break;
                            }
                        }
                    }
                    else
                    {
                        charFound++;
                    }
                }

                if(charFound == upperCase.length())
                {
                    gameModel.setUserQueryInput(upperCase,request[1],request[2],request[3]);
                }
                else{
                    this.updateMessageFromModel("invalid word, turn automatically passed");
                    gameModel.setUserQueryInput("xxx","null","null","null");
                }
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

}