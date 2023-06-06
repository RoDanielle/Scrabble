/*package viewModel;

import model.GameModel;
import model.GuestModel;
import model.HostModel;
import view.View;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
*/
/*
public class ViewModel implements Observer {

    String message;
    // game player info
    String[][] board;
    int score;
    String playerName;
    List<String> tiles;
    GameModel model;

    private BiConsumer<String, CompletableFuture<String>> requestCallback;
    private View view; // Reference to the View instance


    public ViewModel(View view, String name, String ip, String port, boolean isHost, boolean isLocal, int numOfPlayers)
    {
        this.playerName = name;
        this.board =  new String[15][15];
        this.score = 0;
        this.tiles = new ArrayList<>();
        this.message = null;
        this.view = view;

        if(isHost)
        {
            model = new HostModel(name, isLocal, numOfPlayers,this);
            //model.setUserInputCallback(input -> processUserInput(input));
        }

        else
        {
            model = new GuestModel(name,ip,Integer.parseInt(port),this);
            //model.setUserInputCallback(input -> processUserInput(input));
        }

    }



    public void setRequestCallback(BiConsumer<String, CompletableFuture<String>> callback) {
        this.requestCallback = callback;
    }


    public void handleRequestFromModel(String request) {
        if (requestCallback != null) {
            // Create a CompletableFuture to receive the answer from the Model
            CompletableFuture<String> userInputFuture = new CompletableFuture<>();

            // Show different input boxes based on the request
            switch (request) {
                case "name":
                    // Show the name input box in the View and pass the userInputFuture
                    view.showNameInputBox(userInputFuture);
                    break;
                case "wordDetails":
                    // Show the word details input box in the View and pass the userInputFuture
                    view.showWordDetailsInputBox(userInputFuture);
                    break;
                case "challenge":
                    // Show the challenge input box in the View and pass the userInputFuture
                    view.showChallengeInputBox(userInputFuture);
                    break;
                // Add more cases for different request types
                default:
                    // Invalid request, complete the future with an error
                    userInputFuture.completeExceptionally(new IllegalArgumentException("Invalid request type"));
                    break;
            }

            // Pass the request to the Model
            model.makeRequest(request);

            // Set the userInputFuture in the Model for waiting the input
            model.setUserInputFuture(userInputFuture);
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel)
        {
            if(arg.equals("score"))
            {
                score = model.getScore();
            }
            else if(arg.equals("name"))
            {
                playerName = model.getName();
            }
            else if(arg.equals("board"))
            {
                board = model.getBoard();
            }
            else if(arg.equals("tiles"))
            {
                tiles = model.getTiles();
            }
            else // message
            {
                message = model.getMessage();

                // there will be a small window in the view showing the message.
                // handling requests for input are not done here!!!
            }
        }
    }
}

 */
