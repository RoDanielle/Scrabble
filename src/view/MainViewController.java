package view;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML private ImageView board_init;


    /**
     * The initialize function is called when the program starts. It sets up the
     * dropdown menus for row, column, and vertical/horizontal selection.
     *
     * @param location Determine the location of the fxml file
     * @param resources Get the value of a resource (string, image, locale-specific data, and other
     *
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        row.getItems().addAll(rowAndCol);
        col.getItems().addAll(rowAndCol);
        vertical.getItems().addAll(ver);
    }

    /**
     * The submitWordHandler function is called when the user clicks on the submitWord button.
     * It takes in a word, row, column and vertical value from the user input fields and sends them to
     * viewModel.processQueryInput(userQueryInput) for processing. If an error occurs during this process, it will be caught by
     * handleError(String errorMessage). The function also clears out any text that was entered into word field after submission.
     *
     * @param event Determine which button was pressed
     *
     *
     */
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
        viewModel.processQueryInput(userQueryInput);
    }


    /**
     * The challengeHandler function is called when the user clicks on either the challenge or pass button.
     * It sends a message to the server with either yes or no depending on which button was clicked.
     *
     * @param event Determine which button was pressed
     *
     *
     * @trrows Exception
     */
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


    /**
     * The setViewMode function is used to set the viewModel of the MainViewController.
     * It also sets up all the bindings and listeners for this controller.
     *
     *
     * @param name Set the name of the player
     * @param ip Set the ip address of the host
     * @param port Set the port number of the server
     * @param isHost Determine whether the user is a host or guest
     * @param isLocal Determine if the game is local or remote
     * @param numOfPlayers Set the number of players in the game
     *
     *
     */
    public void setViewMode(String name, String ip, String port, boolean isHost, boolean isLocal, int numOfPlayers) {
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
                submitWord.setDisable(false);
                passTurn.setDisable(false);
            } else {
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

    /**
     * The BindAll function binds the ViewModel to the View.
     * It does this by binding all the properties in the view model to their respective labels and list views.
     * This is done using a bindBidirectional function for score, which allows it to be changed from both sides (the view and viewmodel).
     * The other bindings are one way, meaning that they can only be changed from one side (in this case, only from within the ViewModel).
     *
     *
     *
     */
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

    /**
     * The bindTiles function binds the tiles in the viewModel to their respective labels.
     * It does this by iterating through all the children of tilesListView, and if a child is an instance of Label,
     * it will bind that label's textProperty to its corresponding tile in viewModel.tilesProperty().
     *
     *
     *
     */
    private void bindTiles(){
        int i = 0;

        for (Node node : tilesListView.getChildren()) {
            if (node instanceof Label && i < viewModel.tilesProperty().size()) {
                Label label = (Label) node;
                label.textProperty().bind(viewModel.tilesProperty().get(i));
                i++;
                GridPane.setHalignment(label, HPos.CENTER); // Horizontal alignment
                GridPane.setValignment(label, VPos.CENTER); // Vertical alignment
            }
        }
    }

    /**
     * The bindBoard function binds the board to the view.
     * It does so by iterating over all the labels in BoardGrid, and binding them to their corresponding cell in viewModel.boardProperty().
     *
     *
     *
     */
    private void bindBoard() {
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

    /**
     * The screenInit function is used to set the fullScreen AnchorPane's constraints so that it covers the entire screen.
     * This function is called in the initialize method of this class, and should not be called again after that point.
     *
     *
     *
     */
    private void screenInit(){
        fullScreen.setStyle("-fx-background-color: white;"); // Set a background color for demonstration purposes
        // Set the anchor constraints to cover the entire screen
        AnchorPane.setTopAnchor(fullScreen, 0.0);
        AnchorPane.setRightAnchor(fullScreen, 0.0);
        AnchorPane.setBottomAnchor(fullScreen, 0.0);
        AnchorPane.setLeftAnchor(fullScreen, 0.0);

    }

    /**
     * The createTiles function creates the tilesListView and adds it to the root.
     * It also sets its style, layoutX, layoutY, and gridLinesVisible properties.
     * Finally, it adds a label to each column of tilesListView with text index i, where i is the index of that column.
     *
     *
     *
     */
    private void createTiles() {
        tilesListView.addRow(0);
        for (int i = 0; i < 7; i++) {
            tilesListView.addColumn(i);
        }
        tilesListView.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-background-color: beige;");
        tilesListView.setLayoutX(70);
        tilesListView.setLayoutY(718);
        tilesListView.setGridLinesVisible(true);

        for (int i = 0; i < 7; i++) {
            Label label = new Label();
            label.setStyle("-fx-font-size: 24px; -fx-text-fill: black;");
            label.setText("index " + i);
            tilesListView.add(label, i, 0);
        }
    }

    /**
     * The creatBoard function creates the board for the game.
     * It sets up a grid pane and adds columns and rows to it.
     * Then, it loops through each row and column of the grid pane, adding labels to each cell in order to color them appropriately.
     *
     *
     *
     */
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
               BoardGrid.add(label, i, j);
               //BoardGrid.add(setColor(i,j,label), i, j);
               label.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
           }
       }
    }

    /**
     * The setColor function takes in the row and column of a label, and sets its color based on some conditions:
     *
     * @param i Represent the row number of the board
     * @param j Represent the column and the int i parameter is used to represent the row
     * @param label Set the color of the label
     *
     * @return Label
     *
     */
    private Label setColor(int i,int j,Label label){
        if(i == 7 && j == 7)
        {
            label.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-background-color: orange;");
        }
        else if(isRed(i,j))
        {
            label.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-background-color: red;");
        }
        else if(isYellow(i,j))
        {
            label.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-background-color: yellow;");
        }
        else if(isBlue(i,j))
        {
            label.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-background-color: blue;");
        }
        else if(isBabyBlue(i,j))
        {
            label.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-background-color: lightBlue;");
        }
        else
            label.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-background-color: green;");


        return label;
    }

    private boolean isRed(int i, int j)
    {
        if( i == j && i == 7)
            return false;

        if((i % 7 == 0 && j % 7 == 0))
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
        if((i == 1 && (j == 5 || j == 9)) || (i == 5 && (j == 1 || j == 5 || j == 9 || j== 13)) || (i == 9 && (j == 1 || j == 5 || j == 9 || j== 13)) || (i == 13 && (j == 5 || j == 9)))
            return true;

        return false;
    }

    private boolean isBabyBlue(int i, int j)
    {
        if(((i == 8 || i == 6) && (j == 8 || j == 6)) || ((i == 3 || i == 11) && (j % 7 == 0)) || ((j == 3 || j == 11) && i% 7 == 0) ||((i == 2 || i == 12) && (j== 6 || j ==8)) || ((i == 8 || i == 6) && (j == 2 || j == 12)))
            return true;

        return false;
    }

    /**
     * The UILoarder function is responsible for initializing the screen, creating tiles and creating a board.
     *
     *
     */
    private void UILoarder() {
        loadImage();
        screenInit();
        createTiles();
        creatBoard();
    }

    private void loadImage() {
        Image board_image = new Image(getClass().getResourceAsStream("/view/images/board.jpeg"));
        board_init.setImage(board_image);
    }
}