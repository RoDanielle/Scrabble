package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApplication extends Application {

    /**
     * The start function is the main function of the application. It loads
     * a WelcomeScreenController and displays it to the user.
     *
     * @param primaryStage Set the scene, and to show it
     *
     *
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource("/view/WelcomeScreen.fxml"));
        AnchorPane root = loader.load();
        WelcomeScreenController controller = loader.getController();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * The main function of the program.
     *
     *
     * @param args Pass arguments to the application
     *
     *
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }
}