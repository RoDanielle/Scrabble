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

        // main view
        Stage stage = (Stage) submit.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        stage.setTitle("Game");
        MainViewController view = loader.getController();
        view.setViewMode(s_hostName,"null","null", this.isHost, this.isLocal, Integer.parseInt(num));
        stage.setScene(new Scene(root));
    }

   public void setInfo(boolean host, boolean local)
   {
       this.isHost = host;
       this.isLocal = local;
   }
}
