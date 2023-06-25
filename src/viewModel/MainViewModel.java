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

    /**
     * The initializeTiles function initializes the tilesProperty ArrayList with 7 SimpleStringProperties.
     * This is done so that we can bind the text of each tile to a StringProperty in this list, which will allow us to update
     * the text of each tile from anywhere in our code.
     *
     *
     */
    private void initializeTiles()
    {
        for(int i = 0; i < 7; i++)
        {
            StringProperty strprop = new SimpleStringProperty();
            tilesProperty.add(strprop);
        }
    }

    /**
     * The initializeBoard function initializes the boardProperty array with 15 rows and 15 columns.
     * Each element in the array is a StringProperty object that contains an empty string.
     *
     *
     */
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


    /**
     * The update function is called whenever the gameModel changes.
     * It updates the view to reflect any changes in the model.
     *
     *
     * @param o Determine which observable object called the update function
     * @param arg Determine what part of the model is being updated
     *
     *
     */
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

    /**
     * The isUserTurnProperty function returns a BooleanProperty that is true if it is the user's turn, and false otherwise.
     *
     *
     * @return A booleanproperty object
     *
     */
    public BooleanProperty isUserTurnProperty() {
        return isUserTurn;
    }

    /**
     * The isUserchallengeProperty function returns a BooleanProperty object that represents the isUserChallenge property.
     *
     *
     * @return A booleanproperty
     *
     */
    public BooleanProperty isUserchallengeProperty() {
        return isUserChallenge;
    }
    /**
     * The startUserQueryTurn function sets the isUserTurn boolean to true, which allows the user to enter a query.
     *
     *
     */
    public void startUserQueryTurn() {
        isUserTurn.set(true);
    }

    /**
     * The startUserChallengeTurn function sets the isUserChallenge boolean to true.
     *
     *
     */
    public void startUserChallengeTurn() {
        isUserChallenge.set(true);
    }


    /**
     * The processQueryInput function is called by the View when the user enters a word, row, column and orientation.
     * The function checks if it is currently the user's turn to play. If so, it sets up an array of strings that contains
     * each character in the word entered by the user. It then iterates through this array and checks if each character exists
     * in any of our tiles on hand (the ones displayed on screen). If all characters exist in our tiles on hand, we set up a new string with these characters removed from their original positions (so they can't be used again) and pass this string to gameModel
     *
     * @param userInput Get the user's input from the view
     *
     */
    public void processQueryInput(String userInput) {  //  word|row|col|vertical from View
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

    /**
     * The processChallengeInput function is called when the user has been challenged by another player.
     * The function takes in a String as input, which represents the user's response to the challenge.
     * If the user responds with &quot;yes&quot;, then we set their challenge input to &quot;C&quot;. Otherwise, we set it to &quot;xxx&quot;.
     *
     * @param userInput Get the user's input from the gui
     *
     *
     */
    public void processChallengeInput(String userInput) {
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
    /**
     * The updateTilesFromModel function is called by the model when it has been updated.
     * It takes a list of strings as an argument, and updates the tilesProperty list with these values.
     *
     * @param updatedTiles Update the tilesproperty list
     *
     *
     */
    public void updateTilesFromModel(List<String> updatedTiles) {
        int i = 0;
        for(String s : updatedTiles)
        {
            this.tilesProperty.get(i).setValue(s);
            i++;
        }
    }

    /**
     * The tilesProperty function returns a list of StringProperty objects.
     *
     *
     * @return A list of stringproperty objects
     *
     */
    public List<StringProperty> tilesProperty() {
        return tilesProperty;
    }

    // score update and get
    /**
     * The updateScoreFromModel function is called by the model when it changes.
     * It updates the scoreProperty to reflect the new value of score in the model.
     *
     * @param score Set the scoreproperty
     *
     *
     */
    public void updateScoreFromModel(int score) {
        scoreProperty.set(score);
    }

    /**
     * The scoreProperty function returns the scoreProperty of the player.
     *
     *
     * @return A property, which is a variable that can be observed
     *
     */
    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    // name update and get
    /**
     * The updateNameFromModel function is called by the model when it changes.
     * It updates the nameProperty to reflect this change.
     *
     * @param name Set the nameproperty
     *
     *
     */
    public void updateNameFromModel(String name)
    {
        nameProperty.set(name);
    }

    /**
     * The nameProperty function returns the nameProperty of the object.
     *
     *
     * @return A stringproperty
     *
     */
    public StringProperty nameProperty() {
        return nameProperty;
    }

    // message update and get
    /**
     * The updateMessageFromModel function is a function that updates the message from the model.
     *
     *
     * @param msg Set the value of msgproperty
     *
     *
     */
    public void updateMessageFromModel(String msg)
    {
        msgProperty.set(msg);
    }
    /**
     * The MsgProperty function returns the msgProperty variable.
     *
     *
     * @return A stringproperty object that is bound to the msgproperty variable
     *
     */
    public StringProperty MsgProperty() {
        return msgProperty;
    }

    // board update and get
    /**
     * The boardProperty function is a getter for the boardProperty array.
     *
     *
     *
     * @return A 2d array of stringproperty objects
     *
     */
    public StringProperty[][] boardProperty() {
        return boardProperty;
    }

    /**
     * The updateBoardFromModel function updates the boardProperty array with the values from updatedBoard.
     *
     *
     * @param updatedBoard Update the boardproperty[][]
     *
     *
     */
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