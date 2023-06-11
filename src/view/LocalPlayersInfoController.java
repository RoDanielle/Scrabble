package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LocalPlayersInfoController implements Initializable {
    private boolean isHost;
    private boolean isLocal;
    private String allNames;

    private int numOfPlayers;
    private List<TextField> textFieldList;

    @FXML
    private AnchorPane container; // Assuming you have an AnchorPane container in your FXML file

    @FXML
    private Button submitNames;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldList = new ArrayList<>();
    }

    @FXML
    private void localNamesHandler(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < textFieldList.size(); i++) {
            TextField textField = textFieldList.get(i);
            String name = textField.getText();
            builder.append(name);
            if (i < textFieldList.size()) {
                builder.append("|");
            }
        }
        allNames = builder.toString();
        System.out.println(allNames); // For debugging purposes
    }


    public void setInfo(boolean host, boolean local, int num) {
        this.isHost = host;
        this.isLocal = local;
        this.numOfPlayers = num;
    }

    public AnchorPane getContainer() {
        return container;
    }

    public void createTextFields() {
        container.getChildren().clear();
        textFieldList.clear();
        double initialY = 20.0; // Initial Y position for the first TextField
        double spacing = 20.0; // Spacing between TextField elements
        for (int i = 0; i < numOfPlayers; i++) {
            TextField textField = new TextField();
            textFieldList.add(textField);
            textField.setLayoutY(initialY + (i * spacing));
            container.getChildren().add(textField);
        }
    }
}
