package view;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;
import viewModel.MainViewModel;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable{

    private MainViewModel viewModel;
    @FXML AnchorPane fullScreen;

    // binded vars with the viewmodel
    @FXML
    private Label msgLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private GridPane BoardGrid;
    @FXML
    private GridPane tilesListView;

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

    // var for user challenge
    @FXML private Button challengeB;
    @FXML private Button passChallenge;


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

            word.clear();
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


    @FXML
    private void challengeHandler (ActionEvent event) throws Exception { //word|row|col|vertical
        String challengeInput;

        if(event.getSource()==challengeB)
        {
            challengeInput = "yes";
        }
        else // pass
        {
            challengeInput = "no";
        }
        viewModel.processChallengeInput(challengeInput);
    }

    // activation buttons for query and challenge (triggered activation from view model)

    private BooleanProperty isUserTurnProperty;
    private BooleanProperty isUserChallengeProperty;


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
            this.viewModel = new MainViewModel(name, ip, port, false, false, 1);
        }

        this.UILoarder();
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
                System.out.println("view got user challenge true");
                challengeB.setDisable(false);
                passChallenge.setDisable(false);
            } else {
                System.out.println("view got user challenge false");
                challengeB.setDisable(true);
                passChallenge.setDisable(true);
            }
        });
    }

    private void BindAll() {

        //NAME - Bind name
        nameLabel.textProperty().bind(viewModel.nameProperty());
        //SCORE - Bind score
        scoreLabel.textProperty().bindBidirectional(viewModel.scoreProperty(), new NumberStringConverter());
        // MESSAGE - Bind message
        msgLabel.textProperty().bind(viewModel.MsgProperty());
        //TILES - Bind the items of the ListView to the tilesProperty in the ViewModel
        this.bindTiles();
        // Bind board
        this.bindBoard();

        // Bind query and challenge (will not show to the user)
        this.isUserTurnProperty = viewModel.isUserTurnProperty();
        this.isUserChallengeProperty = viewModel.isUserchallengeProperty();
    }

    private void bindTiles(){
        System.out.println("entered bind tiles func");
        int i = 0;

        for (Node node : tilesListView.getChildren()) {
            if (node instanceof Label && i < viewModel.tilesProperty().size()) {
                Label label = (Label) node;
                label.textProperty().bind(viewModel.tilesProperty().get(i));
                i++;
            }
        }
    }

    private void bindBoard()
    {
        int i = 0;
        int j = 0;
        for (Node node : BoardGrid.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.textProperty().bind(viewModel.boardProperty()[i][j]);
                    i++;
                if(i == 15)
                {
                    i = 0;
                    j++;
                }
            }
        }
    }

    private void screenInit(){
        fullScreen.setStyle("-fx-background-color: white;"); // Set a background color for demonstration purposes
        // Set the anchor constraints to cover the entire screen
        AnchorPane.setTopAnchor(fullScreen, 0.0);
        AnchorPane.setRightAnchor(fullScreen, 0.0);
        AnchorPane.setBottomAnchor(fullScreen, 0.0);
        AnchorPane.setLeftAnchor(fullScreen, 0.0);

    }

    private void createTiles() {
        tilesListView.addRow(0);
        for (int i = 0; i < 7; i++) {
            tilesListView.addColumn(i);
        }
        tilesListView.setStyle("-fx-font-size: 16px; -fx-text-fill: green; -fx-background-color: pink;");
        tilesListView.setLayoutX(70);
        tilesListView.setLayoutY(718);
        tilesListView.setGridLinesVisible(true);

        for (int i = 0; i < 7; i++) {
            Label label = new Label();
            label.setStyle("-fx-font-size: 16px; -fx-text-fill: blue; -fx-background-color: yellow;");
            label.setText("index " + i);
            tilesListView.add(label, i, 0);
        }
    }

    private void creatBoard(){
        for (int i = 0; i < 15; i++) {
            BoardGrid.addColumn(i);
            BoardGrid.addRow(i);
        }
        //BoardGrid.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-background-color: lightGrey;");
        BoardGrid.setLayoutX(28);
        BoardGrid.setLayoutY(84);
        BoardGrid.setGridLinesVisible(true);

       for(int i = 0; i < 15; i ++)
       {
           for(int j = 0; j < 15; j++)
           {
               Label label = new Label();
               BoardGrid.add(setColor(i,j,label), i, j);
           }
       }
    }

    private Label setColor(int i,int j,Label label){
        if(i == 7 && j == 7)
        {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-background-color: gold;");
        }
        else if(isRed(i,j))
        {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-background-color: red;");
        }
        else if(isYellow(i,j))
        {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-background-color: yellow;");
        }
        else if(isBlue(i,j))
        {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-background-color: blue;");
        }
        else if(isBabyBlue(i,j))
        {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-background-color: lightBlue;");
        }
        else
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-background-color: green;");

        return label;
    }

    private boolean isRed(int i, int j)
    {
        if((i % 7 == 0 && j % 7 == 0) && i != 7 && j!= 7)
            return true;

        return false;
    }

    private boolean isYellow(int i, int j)
    {
        if(((0<i && i<5) && (i==j || i+j == 14)) || ((9 < i && i < 14) && (i == j || i + j == 14)))
            return true;

        return false;
    }

    private boolean isBlue(int i, int j)
    {
        if(((i == 1 || i == 3) && (j == 5 || j == 9)) || ((i== 5 || i == 9) && (j == 1 || j == 5 || j == 9 || j==13)))
            return true;

        return false;
    }

    private boolean isBabyBlue(int i, int j)
    {
        if(((i == 8 || i == 6) && (j == 8 || j == 6)) || ((i == 3 || i == 11) && (j % 7 == 0)) || ((j == 3 || j == 11) && i% 7 == 0) ||((i == 2 || i == 12) && (j== 6 || j ==8)) || ((i == 8 || i == 6) && (j == 2 || j == 12)))
            return true;

        return false;
    }

    private void UILoarder()
    {
        screenInit();
        createTiles();
        creatBoard();
    }

}