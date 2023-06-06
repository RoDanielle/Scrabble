package view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.util.converter.NumberStringConverter;
import viewModel.MainViewModel;
import javafx.scene.control.ListView;
import javafx.beans.property.StringProperty;


import java.util.Scanner;

public class MainViewController {
    @FXML
    private Label msgLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label[][] labelGrid;
    @FXML
    private ListView<String> tilesListView;
    @FXML
    private Button userQueryButton;
    @FXML
    private Button userChallengeButton;

    private MainViewModel viewModel;
    private BooleanProperty isUserTurnProperty;

    private BooleanProperty isUserChallengeProperty;



    public MainViewController() {
        setupUI();
    }

    private void setupUI() {
        // Set up your JavaFX UI components
        // ...
        start123();
        BindAll();

    /*
    // set buttons for viewmodel triggered actions
        // Listen to user input events (e.g., button clicks)
        userQueryButton.setOnAction(event -> {
            String userInput = // Extract user input from the UI componentview
            //viewModel.processQueryInput(userInput);
            // ....
        });

        userChallengeButton.setOnAction(event -> {
            String userInput = // Extract user input from the UI componentview
            //viewModel.processChallengeInput(userInput);
            // ....
        });


        // Observe the ViewModel's properties
        viewModel.isUserTurnProperty().addListener((observable, oldValue, newValue) -> { // turn (query) "notifier"
            if (newValue) {
                // It's the user's turn
                // Update the UI accordingly
                // ...
            } else {
                // It's not the user's turn
                // Update the UI accordingly
                // ...
            }
        });

           viewModel.isUserchallengeProperty().addListener((observable, oldValue, newValue) -> { // challenge "notifier"
            if (newValue) {
                // It's the user's turn
                // Update the UI accordingly
                // ...
            } else {
                // It's not the user's turn
                // Update the UI accordingly
                // ...
            }
        });

     */

        /*
        // Update the UI components based on changes to the board property
        viewModel.boardProperty().addListener((observable, oldValue, newValue) -> {
            updateBoardUI(newValue);
        });





        */

    }

    public void start123(){
        boolean ishost;
        boolean islocal;
        String num = "null";
        String ip = "null";
        String port = "null";
        String playerType;
        String gameType;

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter your name");
        String name = input.nextLine();

        System.out.println("What type of player are you? Enter 1 for Host, Enter 2 for Guest");
        playerType = input.nextLine();

        if (playerType.equals("1")){ // HOST
            ishost = true;
            System.out.println("you are now in Host mode");
            System.out.println("Do you want to play a local game or host a remote game? for local enter: 1, for remote enter: 2");
            gameType = input.nextLine();
            System.out.println("How many player including you are playing? 1-4");
            num = input.nextLine();

            if(gameType.equals("1")) // LOCAL
            {
                islocal = true;
                for(int i = 2; i < Integer.parseInt(num) + 1; i++)
                System.out.println("Please enter player " + i + " name");
                name = name + "|" +  input.nextLine();
            }
            else { // REMOTE
                islocal = false;
            }
        }
        else { // GUEST
            ishost = false;
            islocal = false;
            System.out.println("you are now in Guest mode");
            System.out.println("Please enter server ip");
            ip = input.nextLine();
            System.out.println("Please enter server port");
            port = input.nextLine();

        }
        // TODO - create the viewmodel object after a startgame button was pressed
        this.viewModel = new MainViewModel(name, ip, port, ishost, islocal, Integer.parseInt(num));
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



    public void BindAll() {


        //NAME - Bind name
        nameLabel.textProperty().bind(viewModel.nameProperty());
        //SCORE - Bind score
        scoreLabel.textProperty().bindBidirectional(viewModel.scoreProperty(), new NumberStringConverter());
        // MESSAGE - Bind message
        msgLabel.textProperty().bind(viewModel.MsgProperty());
        //TILES - Bind the items of the ListView to the tilesProperty in the ViewModel
        Bindings.bindContent(tilesListView.getItems(), viewModel.tilesProperty());
        // Bind board
        this.initializeBoard();

        // Bind query and challenge (will not show to the user)
        this.isUserTurnProperty = viewModel.isUserTurnProperty();
        this.isUserChallengeProperty = viewModel.isUserchallengeProperty();
    }

    public void initializeBoard() {
        String[][] initialBoard = new String[15][15];
        viewModel.updateBoardFromModel(initialBoard);

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
}