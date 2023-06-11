package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @FXML
    private void handleModeAction (ActionEvent event) throws Exception {
        Stage stage;
        Parent root;

        if(event.getSource()==local){
            this.isLocal = true;
            stage = (Stage) local.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HostLocaIInfo.fxml"));
            root = loader.load();
            stage.setTitle("Local game info");
            HostLocalInfoController localController = loader.getController();
            localController.setInfo(this.isLocal,this.isHost); // Pass the boolean parameter here
        }

        else{ //remote
            this.isLocal = false;

            stage = (Stage) remote.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HostRemoteInfo.fxml"));
            root = loader.load();
            stage.setTitle("Remote game info");
            HostRemoteInfoController remoteController = loader.getController();
            remoteController.setInfo(this.isLocal,this.isHost); // Pass the boolean parameter here
        }
        stage.setScene(new Scene(root));
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }
}




