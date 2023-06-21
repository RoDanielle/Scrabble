package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LocalPlayersInfoController implements Initializable {
    private boolean isHost;
    private boolean isLocal;
    private String allNames;

    private int numOfPlayers;
    private List<TextField> textFieldList;

    @FXML
    private AnchorPane container; // Assuming you have an AnchorPane container in your FXML file

    @FXML
    private Button submitNames;
    @FXML
    private ImageView names;

    /**
     * The initialize function is called when the FXML file is loaded.
     * It sets up the textFieldList arraylist, which will be used to store all the TextFields in this scene.

     *
     * @param location Determine the location of the fxml file
     * @param resources Load the strings from the resource bundle
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImage();
        textFieldList = new ArrayList<>();
    }

    private void loadImage()
    {
        Image namesImage = new Image(getClass().getResourceAsStream("/view/images/names.jpeg"));
        names.setImage(namesImage);
    }

    /**
     * The localNamesHandler function is responsible for taking the names of all players and passing them to the MainViewController.
     * It also passes a boolean value that indicates whether this game is local, as well as a boolean value indicating if this player
     * is hosting the game. The number of players in the game are also passed to MainViewController so that it can be used later on in determining
     * which player's turn it currently is. Finally, we set up our main view by loading its FXML file and setting its title before displaying it on screen.

     *
     * @param event Get the source of the event

     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @FXML
    private void localNamesHandler(ActionEvent event) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < textFieldList.size(); i++) {
            TextField textField = textFieldList.get(i);
            String name = textField.getText();
            builder.append(name);
            if (i < textFieldList.size()) {
                builder.append("|");
            }
        }
        allNames = builder.toString();
        System.out.println(allNames); // For debugging purposes

        // main view
        Stage stage = (Stage) submitNames.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        stage.setTitle("Scrabble Game");
        MainViewController view = loader.getController();
        view.setViewMode(allNames,"null","null", this.isLocal,this.isHost, numOfPlayers); // Pass the boolean parameter here
        stage.setScene(new Scene(root));
    }


    /**
     * The setInfo function sets the isHost, isLocal, and numOfPlayers variables to
     * the values passed in as parameters.
     *
     *
     * @param host Determine whether the player is a host
     * @param local Determine whether the game is local or not
     * @param num Set the number of players in the game
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void setInfo(boolean host, boolean local, int num) {
        this.isHost = host;
        this.isLocal = local;
        this.numOfPlayers = num;
    }

    public AnchorPane getContainer() {
        return container;
    }

    /**
     * The createTextFields function creates a TextField for each player in the game.
     * The number of players is determined by the numOfPlayers variable, which is set
     * when the user selects a difficulty level from the menu.  The function also adds
     * these TextFields to an ArrayList called textFieldList, so that they can be accessed later.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void createTextFields() {
        container.getChildren().clear();
        textFieldList.clear();
        double initialY = 20.0; // Initial Y position for the first TextField
        double spacing = 50.0; // Spacing between TextField elements
        for (int i = 0; i < numOfPlayers; i++) {
            TextField textField = new TextField();
            textFieldList.add(textField);
            textField.setLayoutY(initialY + (i * spacing));
            container.getChildren().add(textField);
        }
    }
}
