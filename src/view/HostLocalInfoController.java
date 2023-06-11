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
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class HostLocalInfoController implements Initializable {

    private boolean isHost;
    private boolean isLocal;
    private String s_hostName;
    private String num;

    @FXML private Button submit;
    @FXML private Label question;
    //@FXML private Label name;
    @FXML private ChoiceBox<String> numbers;
    private String[] nums = {"1","2","3","4"};


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numbers.getItems().addAll(nums);
    }

    @FXML
    private void HostLocalAction (ActionEvent event) throws Exception {
        num = numbers.getValue();
        System.out.println(s_hostName);
        System.out.println(num);

        Stage stage = (Stage) submit.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LocalPlayersInfo.fxml"));
        Parent root = loader.load();
        stage.setTitle("Players Names");
        LocalPlayersInfoController localPlayersController = loader.getController();
        localPlayersController.setInfo(this.isLocal,this.isHost, Integer.parseInt(this.num)); // Pass the boolean parameter here
        localPlayersController.createTextFields();
        stage.setScene(new Scene(root));
    }

    public void setInfo(boolean host, boolean local)
    {
        this.isHost = host;
        this.isLocal = local;
    }
}
