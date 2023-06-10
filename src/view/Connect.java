package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.event.ActionEvent;

public class Connect {

    @FXML public TextField port;
    @FXML public TextField ip;
    @FXML public TextField name;
    @FXML private Button connect;
    @FXML private Label title;
    @FXML private Label enter_ip;
    @FXML private Label enter_port;
    @FXML private Label enter_name;
    public String s_port,s_ip,s_name;
    @FXML
    private void initialize() {
        connect.setOnAction(e -> {
            s_ip = ip.getText();
            s_port = port.getText();
            s_name = name.getText();

            System.out.println(s_ip);
            System.out.println(s_port);
            System.out.println(s_name);
        });
    }

}
