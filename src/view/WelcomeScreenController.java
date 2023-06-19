package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class WelcomeScreenController {
    @FXML
    private Label header;
    @FXML
    private Label playerType;
    @FXML
    private Button host;
    @FXML
    private Button guest;
    private boolean isHost;



    /**
     * The handleButtonAction function is a function that handles the button action of the host and guest buttons.
     * It sets up a new scene for either GameMode or Connect depending on which button was pressed.
     *
     *
     * @param event Get the source of the button that was clicked
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @FXML
    private void handleButtonAction (ActionEvent event) throws Exception {
        Stage stage;
        Parent root;

        if(event.getSource()==host){
            this.isHost = true;
            stage = (Stage) host.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameMode.fxml"));
            root = loader.load();
            stage.setTitle("Game Mode");

            GameModeController GameModeController = loader.getController();
            GameModeController.setIsHost(true); // Pass the boolean parameter here

        }
        else{ //guest
            this.isHost = false;
            stage = (Stage) guest.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Connect.fxml"));
            root = loader.load();
            stage.setTitle("Connect");

            ConnectController connectController = loader.getController();
            connectController.setIsHost(false); // Pass the boolean parameter here

        }
        stage.setScene(new Scene(root));
    }
}
