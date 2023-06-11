package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;




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

            System.out.println(s_ip);
            System.out.println(s_port);
            System.out.println(s_name);

            // here pass all strings and bool to mainView
            //MainViewController.setGuestInfo(s_name,s_ip,s_port,isHost);
        });
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

}
