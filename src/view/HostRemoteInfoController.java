package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class HostRemoteInfoController implements Initializable {

    private boolean isHost;
    private boolean isLocal;
    private String s_hostName;
    private String num;

    @FXML public TextField hostName;
    @FXML private Button submit;
    @FXML private Label question;
    @FXML private Label name;
    @FXML private ChoiceBox<String> numbers;
    private String[] nums = {"2","3","4"};


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numbers.getItems().addAll(nums);
    }

    @FXML
    private void HostRemoteAction (ActionEvent event) throws Exception {
        s_hostName = hostName.getText();
        num = numbers.getValue();
    }

   public void setInfo(boolean host, boolean local)
   {
       this.isHost = host;
       this.isLocal = local;
   }
}
