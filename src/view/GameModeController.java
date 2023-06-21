package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class GameModeController {
    private boolean isHost;
    private boolean isLocal;
    @FXML
    private Label playerType;
    @FXML
    private Button local;
    @FXML
    private Button remote;

    @FXML private ImageView localIm;
    @FXML private ImageView remoteIm;

    @FXML
    private void initialize() {
        loadImage();
    }

    private void loadImage() {
        Image localImage = new Image(getClass().getResourceAsStream("/view/images/local.jpeg"));
        localIm.setImage(localImage);
        Image remoteImage = new Image(getClass().getResourceAsStream("/view/images/remote.jpeg"));
        remoteIm.setImage(remoteImage);
    }

    /**
     * The handleModeAction function is called when the user clicks on either the local or remote button.
     * It sets a boolean value to true if the user clicked on local, and false if they clicked on remote.
     * Then it loads up either HostLocalInfoController or HostRemoteInfoController depending on which button was pressed.

     *
     * @param event Get the source of the button that was pressed

     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @FXML
    private void handleModeAction (ActionEvent event) throws Exception {
        Stage stage;
        Parent root;

        if(event.getSource()==local){
            this.isLocal = true;
            stage = (Stage) local.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HostLocalInfo.fxml"));
            root = loader.load();
            stage.setTitle("Local game info");
            HostLocalInfoController localController = loader.getController();
            localController.setInfo(this.isHost,this.isLocal);
        }

        else{ //remote
            this.isLocal = false;

            stage = (Stage) remote.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HostRemoteInfo.fxml"));
            root = loader.load();
            stage.setTitle("Remote game info");
            HostRemoteInfoController remoteController = loader.getController();
            remoteController.setInfo(this.isHost,this.isLocal); // Pass the boolean parameter here
        }
        stage.setScene(new Scene(root));
    }

    /**
     * The setIsHost function sets the isHost variable to true or false.
     *
     *
     * @param isHost Determine whether the user is a host or not
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }
}




