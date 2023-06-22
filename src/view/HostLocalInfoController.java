package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class HostLocalInfoController implements Initializable {

    private boolean isHost;
    private boolean isLocal;
    private String s_hostName;
    private String num;

    @FXML private Button submit;
    @FXML private Label question;
    @FXML private ChoiceBox<String> numbers;
    @FXML private ImageView number;
    @FXML private ImageView name;

    private String[] nums = {"1","2","3","4"};


    /**
     * The initialize function is called when the FXML file is loaded.
     * It sets up the ComboBox with all the numbers from 1 to 4.
     *
     * @param location Determine where the fxml file is located
     * @param resources Load the resources for the application
     *
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImage();
        numbers.getItems().addAll(nums);
    }

    private void loadImage()
    {
        Image numberImage = new Image(getClass().getResourceAsStream("/view/images/number.jpeg"));
        number.setImage(numberImage);
    }

    /**
     * The HostLocalAction function is called when the user clicks on the Host or Local Game button.
     * It takes in an ActionEvent event as a parameter, and returns nothing.
     * The function sets up a new stage for the next scene (LocalPlayersInfo), and passes it information about whether this game is local, if this player is hosting, and how many players are playing.
     *
     * @param event Get the source of the button that was clicked
     *
     */
    @FXML
    private void HostLocalAction (ActionEvent event) throws Exception {
        num = numbers.getValue();

        Stage stage = (Stage) submit.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LocalPlayersInfo.fxml"));
        Parent root = loader.load();
        stage.setTitle("Players Names");
        LocalPlayersInfoController localPlayersController = loader.getController();
        localPlayersController.setInfo(this.isLocal,this.isHost, Integer.parseInt(this.num));
        localPlayersController.createTextFields();
        stage.setScene(new Scene(root));
    }

    /**
     * The setInfo function sets the isHost and isLocal variables to true or false.
     *
     *
     * @param host Determine whether the player is a host or not
     * @param local Determine if the game is local or not
     *
     *
     */
    public void setInfo(boolean host, boolean local)
    {
        this.isHost = host;
        this.isLocal = local;
    }
}
