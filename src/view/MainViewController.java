package view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import viewModel.MainViewModel;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable{

    private MainViewModel viewModel;
    // binded vars with the viewmodel
    @FXML
    private Label msgLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label scoreLabel;
    @FXML GridPane labelGrid;
    //@FXML private Label[][] labelGrid;
    @FXML
    private ListView<String> tilesListView;

    // vars for user word (query)
    @FXML
    private TextField word;
    @FXML private ChoiceBox<String> row;
    @FXML private ChoiceBox<String> col;
    private String[] rowAndCol = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"};
    @FXML private ChoiceBox<String> vertical;
    private String[] ver = {"vertical","horizontal"};
    @FXML private Button submitWord;
    @FXML private Button passTurn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        row.getItems().addAll(rowAndCol);
        col.getItems().addAll(rowAndCol);
        vertical.getItems().addAll(ver);
    }

    @FXML
    private void submitWordHandler (ActionEvent event) throws Exception { //word|row|col|vertical
        String s_word;
        String s_row;
        String s_col;
        String s_vertical;
        if(event.getSource()==submitWord)
        {
            s_word = word.getText();
            s_row = row.getValue();
            s_col = col.getValue();
            s_vertical = vertical.getValue();
        }
        else // pass
        {
            s_word = "xxx";
            s_row = "null";
            s_col = "null";
            s_vertical = "null";
        }
        String userQueryInput = s_word + "|" + s_row + "|" + s_col + "|" + s_vertical;
        System.out.println("FROM VIEW: " + userQueryInput);
        viewModel.processQueryInput(userQueryInput);
    }

    // var for user challenge
    @FXML private Button challengeB;
    @FXML private Button passChallenge;

    @FXML
    private void challengeHandler (ActionEvent event) throws Exception { //word|row|col|vertical
        String challengeInput;

        if(event.getSource()==challengeB)
        {
            challengeInput = "C";
        }
        else // pass
        {
            challengeInput = "xxx";
        }
        viewModel.processQueryInput(challengeInput);
    }

    // activation buttons for query and challenge (triggered activation from view model)

    private BooleanProperty isUserTurnProperty;
    private BooleanProperty isUserChallengeProperty;


    private void setupUI() {
        // Set up your JavaFX UI components

        /*
        // Update the UI components based on changes to the board property
        viewModel.boardProperty().addListener((observable, oldValue, newValue) -> {
            updateBoardUI(newValue);
        });
        */

    }


    public void setViewMode(String name, String ip, String port, boolean isHost, boolean isLocal, int numOfPlayers)
    {
        if(isHost) // host
        {
            if(isLocal) // local
            {
                this.viewModel = new MainViewModel(name, "null", "null", isHost, isLocal, numOfPlayers);
            }
            else // remote
            {
                this.viewModel = new MainViewModel(name, "null", "null", isHost, isLocal, numOfPlayers);
            }
        }
        else // guest
        {
            this.viewModel = new MainViewModel(name, ip, port, isHost, false, 1);
        }

        this.BindAll();


        // Observe the ViewModel's properties
        viewModel.isUserTurnProperty().addListener((observable, oldValue, newValue) -> { // turn (query) "notifier"
            if (newValue) {
                System.out.println("view got user turn true");
                submitWord.setDisable(false);
                passTurn.setDisable(false);
            } else {
                System.out.println("view got user turn false");
                submitWord.setDisable(true);
                passTurn.setDisable(true);
            }
        });

        viewModel.isUserchallengeProperty().addListener((observable, oldValue, newValue) -> { // challenge "notifier"
            if (newValue) {
                challengeB.setDisable(false);
                passChallenge.setDisable(false);
            } else {
                challengeB.setDisable(true);
                passChallenge.setDisable(true);
            }
        });

    }

    /*
    private void updateBoardUI(String[][] board) {
     // Update the UI components based on the board property
    // ...

        boardGridPane.getChildren().clear();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
               Label label = new Label(board[row][col]);
                boardGridPane.add(label, col, row);
            }
     }

    // ...
    }
     */



    private void BindAll() {
        //NAME - Bind name
        nameLabel.textProperty().bind(viewModel.nameProperty());
        //SCORE - Bind score
        scoreLabel.textProperty().bindBidirectional(viewModel.scoreProperty(), new NumberStringConverter());
        // MESSAGE - Bind message
        msgLabel.textProperty().bind(viewModel.MsgProperty());
        //TILES - Bind the items of the ListView to the tilesProperty in the ViewModel
//        Bindings.bindContent(tilesListView.getItems(), viewModel.tilesProperty());
        // Bind board
    //    this.initializeBoard();

        // Bind query and challenge (will not show to the user)
        this.isUserTurnProperty = viewModel.isUserTurnProperty();
        this.isUserChallengeProperty = viewModel.isUserchallengeProperty();
    }

    /*public void initializeBoard() {
       //String[][] initialBoard = new String[15][15]; - check if needed
        //viewModel.updateBoardFromModel(initialBoard); - check if needed

        // Bind the labelGrid to the boardProperty in the ViewModel
        ObjectProperty<StringProperty[][]> boardProperty = viewModel.boardProperty();
        for (int i = 0; i < labelGrid.length; i++) {
            for (int j = 0; j < labelGrid[i].length; j++) {
                int row = i;
                int col = j;
                labelGrid[row][col].textProperty().bindBidirectional(boardProperty.get()[row][col]);
            }
        }
    }
    */

    public void initializeBoard() {
        //String[][] initialBoard = new String[15][15]; - check if needed
        //viewModel.updateBoardFromModel(initialBoard); - check if needed

        // Bind the labelGrid to the boardProperty in the ViewModel
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Label cellLabel = new Label();
                labelGrid.add(cellLabel, i, j);

            }
        }
    }

}