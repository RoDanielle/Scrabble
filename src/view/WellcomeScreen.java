package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;

public class WellcomeScreen {
    @FXML
    private Label header;
    @FXML
    private Label playerType;
    @FXML
    private Button host;
    @FXML
    private Button guest;
    boolean isHost;

    //Scene scene = guest.getScene();
    //Window window = scene.getWindow();
    //Stage stage = (Stage)window;


    @FXML
    private void handleButtonAction (ActionEvent event) throws Exception {
        Stage stage;
        Parent root;

        if(event.getSource()==host){
            this.isHost = true;
            stage = (Stage) host.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("GameMode.fxml"));
            stage.setTitle("game mode");
        }
        else{ //guest
            this.isHost = false;
            stage = (Stage) guest.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Connect.fxml"));
            stage.setTitle("connect");
        }
        stage.setScene(new Scene(root));
    }



}
