package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;


public class ConnectController {

    @FXML public TextField port;
    @FXML public TextField ip;
    @FXML public TextField name;
    @FXML private Button connect;
    @FXML private Label title;
    @FXML private ImageView enter_ip;
    @FXML private ImageView enter_port;
    @FXML private ImageView enter_name;
    private String s_port,s_ip,s_name;
    private boolean isHost;
    /**
     * The initialize function is called when the FXML file is loaded.
     * It sets up the button to call a function when it's clicked.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */

    private void loadImage()
    {
        Image enter_ipImage = new Image(getClass().getResourceAsStream("/view/images/ip.jpeg"));
        enter_ip.setImage(enter_ipImage);
        Image enter_portImage = new Image(getClass().getResourceAsStream("/view/images/port.jpeg"));
        enter_port.setImage(enter_portImage);
        Image enter_nameImage = new Image(getClass().getResourceAsStream("/view/images/name.jpeg"));
        enter_name.setImage(enter_nameImage);
    }

    @FXML
    private void initialize() {
        loadImage();

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

    /**
     * The setIsHost function sets the isHost variable to true or false.
     *
     *
     * @param isHost Set the ishost variable
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

}
