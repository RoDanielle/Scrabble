package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import viewModel.MainViewModel;

import java.util.Scanner;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource("/view/MainView.fxml"));
        VBox root = loader.load();

        MainViewController controller = loader.getController();
        MainViewModel viewModel = new MainViewModel(); // צריך ליצור מופע של VIEWMODEL ולהכניס את הנתונים שקיבלנו תחילה מה VIEW

        controller.setViewModel(viewModel);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void start123(){
        boolean ishost;
        boolean islocal;
        String num = "null";
        String ip = "null";
        String port = "null";
        String playerType;
        String gameType;

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter your name");
        String name = input.nextLine();

        System.out.println("What type of player are you? Enter 1 for Host, Enter 2 for Guest");
        playerType = input.nextLine();

        if (playerType.equals("1")){ // HOST
            ishost = true;
            System.out.println("you are now in Host mode");
            System.out.println("Do you want to play a local game or host a remote game? for local enter: 1, for remote enter: 2");
            gameType = input.nextLine();
            System.out.println("How many player including you are playing? 1-4");
            num = input.nextLine();

            if(gameType.equals("1")) // LOCAL
            {
                islocal = true;

            }
            else { // REMOTE
                islocal = false;
            }
        }
        else { // GUEST
            ishost = false;
            islocal = false;
            System.out.println("you are now in Guest mode");
            System.out.println("Please enter server ip");
            ip = input.nextLine();
            System.out.println("Please enter server port");
            port = input.nextLine();

        }
        // TODO - create the viewmodel object after a startgame button was pressed
        this.viewModel = new MainViewModel(name, ip, port, ishost, islocal, Integer.parseInt(num));
    }
    public static void main(String[] args) throws Exception {
        launch(args);
    }
}