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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HostRemoteInfoController implements Initializable {

    private boolean isHost;
    private boolean isLocal;
    private String s_hostName;
    private String num;

    @FXML public TextField hostName;
    @FXML private Button submit;
    @FXML private Label question;
    @FXML private ChoiceBox<String> numbers;
    @FXML private ImageView number;
    @FXML private ImageView Pname;
    private String[] nums = {"2","3","4"};


    /**
     * The initialize function is called when the FXML file is loaded.
     * It sets up the ComboBox with all the numbers from 2 to 4.
     *
     * @param location Identify the location of the fxml file
     * @param resources Access the resources for localizing and styling the application
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
        Image nameImage = new Image(getClass().getResourceAsStream("/view/images/name.jpeg"));
        Pname.setImage(nameImage);
    }


    /**
     * The HostRemoteAction function is called when the user clicks on the &quot;Host&quot; button.
     * It sets up a new game with the host's name and number of players, then opens up
     * a new window for playing Scrabble.
     *
     * @param event Get the source of the event, which is used to get the stage
     *
     *
     */
    @FXML
    private void HostRemoteAction (ActionEvent event) throws Exception {
        s_hostName = hostName.getText();
        num = numbers.getValue();

        // main view
        Stage stage = (Stage) submit.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        stage.setTitle("Scrabble Game");
        MainViewController view = loader.getController();
        view.setViewMode(s_hostName,"null","null", this.isHost, this.isLocal, Integer.parseInt(num));
        stage.setScene(new Scene(root));
    }

   /**
    * The setInfo function sets the isHost and isLocal variables to true or false.
    *
    *
    * @param host Determine whether the player is a host or not
    * @param local Determine if the game is being played locally or over a network
    *
    *
    */
   public void setInfo(boolean host, boolean local)
   {
       this.isHost = host;
       this.isLocal = local;
   }
}
