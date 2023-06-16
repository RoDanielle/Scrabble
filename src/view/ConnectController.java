package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class ConnectController {

    @FXML public TextField port;
    @FXML public TextField ip;
    @FXML public TextField name;
    @FXML private Button connect;
    @FXML private Label title;
    @FXML private Label enter_ip;
    @FXML private Label enter_port;
    @FXML private Label enter_name;
    private String s_port,s_ip,s_name;
    private boolean isHost;
    @FXML
    private void initialize() {
        connect.setOnAction(e -> {
            s_ip = ip.getText();
            s_port = port.getText();
            s_name = name.getText();


            // main view
            Stage stage = (Stage) connect.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                stage.setTitle("Scrabble Game");
                MainViewController view = loader.getController();
                view.setViewMode(s_name,s_ip,s_port,isHost,false,1);
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

}
