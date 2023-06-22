/*
model:
case 0: s- 0|a,5.d,8.l,1......
        c - gets seven tiles at the beginning of the game sends nothing back

case 1: c - 1|word(not full)|row|col|v/h|name for the word it wants to enter or 1|xxx|name if wants to pass

case 2:s - there are three options for response: a - query and try... were good: "2|true|score|a,1.b2....|name|word(not full)|row|col|v\h"
                                                  b - only query was good: "2|true|0|name"
                                                  c - query returned false: "2|false|name"
       c - if query false challenge option presented:for challenge sends  3|c|word(not full)|row|col|v/h|name for passing sends 3|xxx|word(not full)|row|col|v/h|name to host

       *** in case 2 we get from host words that were entered and put onto the board by other users in order to put into other players board and maintain an updated board for all

case 3: s - challenge request answer, again three options: a - challenge and try... were good: "3|true|score|a,1.b2....|name|word(not full)|row|col|v\h"
                                                  b - only challenge was good: "3|true|0|name"
                                                  c - challenge returned false: "3|false|name"
case 4 : winner announcement
 */


package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class GuestModel extends Observable implements GameModel{

    private Player guest_player;
    private String[][] board;
    private Map<String,String> letterToScore;
    private String request_to_server;
    private String message;
    private boolean gameRunning;
    private List<Observer> myObservers;

    /**
     * The GuestModel function is the constructor for the GuestModel class.
     * It initializes all the variables that are used in this class, and it also starts a new thread to connect to server in order for the game to start.

     *
     * @param name Set the name of the player
     * @param ip Set the ip address of the server
     * @param port Connect to the server at this given port
     *
     */
    public GuestModel(String name, String ip, int port){
        this.myObservers = new ArrayList<>();
        this.guest_player = new Player();
        this.setName(name);
        this.board = new String[15][15];
        this.message = null;
        this.gameRunning = true;
        this.request_to_server = null;
        this.letterToScore = new HashMap<>();
        letterToScore.put("A","1");
        letterToScore.put("B","3");
        letterToScore.put("C","3");
        letterToScore.put("D","2");
        letterToScore.put("E","1");
        letterToScore.put("F","4");
        letterToScore.put("G","2");
        letterToScore.put("H","4");
        letterToScore.put("I","1");
        letterToScore.put("J","8");
        letterToScore.put("K","5");
        letterToScore.put("L","1");
        letterToScore.put("M","3");
        letterToScore.put("N","1");
        letterToScore.put("O","1");
        letterToScore.put("P","3");
        letterToScore.put("Q","10");
        letterToScore.put("R","1");
        letterToScore.put("S","1");
        letterToScore.put("T","1");
        letterToScore.put("U","1");
        letterToScore.put("V","4");
        letterToScore.put("W","4");
        letterToScore.put("X","8");
        letterToScore.put("Y","4");
        letterToScore.put("Z","10");
        new Thread(()-> {
            this.connectToServer(ip,port);
        }).start();
    }

    /**
     * The getName function returns the name of the guest player.
     *
     *
     * @return The name of the guest player
     */
    @Override
    public String getName() {
        return this.guest_player.name;
    }

    /**
     * The getScore function returns the score of the guest player.
     *
     *
     * @return The score of the player
     */
    @Override
    public int getScore() {
        return this.guest_player.getScore();
    }

    /**
     * The getTiles function returns a list of strings that represent the tiles
     * currently held by the player.
     *
     * @return The list of tiles that the player has
     */
    @Override
    public List<String> getTiles() {
        return this.guest_player.strTiles;
    }

    /**
     * The getMessage function returns the message that is changing from time to time.
     * This message will be shown to the user during the game.
     *
     *
     * @return The message that needs to be shown on screen.
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * The setUserQueryInput function is used to set the user's input for a word, row, column and vertical/horizontal.
     * It sends the players (user) word to the game server for validation according to the games rules.
     * If the user chose to pass its turn (xxx) it sets the message on screen to be "turn over".
     *
     * @param word Store the word that is inputted by the user
     * @param row Set the row of the word to be placed on the board
     * @param col Set the column of the word to be placed on the board
     * @param vertical Indicate whether the word is horizontal or vertical
     *
     */
    @Override
    public void setUserQueryInput(String word, String row, String col, String vertical) {
        this.guest_player.wordDetails[0] = word;
        if(!word.equals("xxx") && !word.equals("XXX")) {
            this.guest_player.wordDetails[1] = row;
            this.guest_player.wordDetails[2] = col;
            this.guest_player.wordDetails[3] = vertical;
        }
        else {
            this.setMessage("turn over");
            this.guest_player.wordDetails[1] = "null";
            this.guest_player.wordDetails[2] = "null";
            this.guest_player.wordDetails[3] = "null";
        }

        request_to_server = "1|" + this.guest_player.wordDetails[0] + "|" +  this.guest_player.wordDetails[1] + "|" + this.guest_player.wordDetails[2] + "|" + this.guest_player.wordDetails[3] + "|" + this.getName();
        write_to_server(this.request_to_server,this.getMySocket());
    }

    /**
     * The setUserChallengeInput function is used to set the user's challenge input.
     * It sends the input to the game server. If the user chose not to challenge it sets the message on screen to be "turn over".
     *
     * @param request Get the user's input for the offer of challenge (a specific move in the game)
     *
     */
    @Override
    public void setUserChallengeInput(String request) {
        if(request.equals("c") || request.equals("C"))
        {
            this.request_to_server = "3" + "|" + "C" + "|" + this.guest_player.wordDetails[0] + "|"+  this.guest_player.wordDetails[1] + "|" + this.guest_player.wordDetails[2] + "|" + this.guest_player.wordDetails[3] + "|" + this.getName();
            write_to_server(request_to_server,this.getMySocket()); // request challenge or not
        }
        else
        {
            this.setMessage("turn over");
            write_to_server(request_to_server,this.getMySocket()); // request challenge or not
        }
    }

    /**
     * The getBoard function returns the board.
     *
     *
     * @return The board array (matrix)
     */
    @Override
    public String[][] getBoard() {
        return this.board;
    }

    /**
     * The setMessage function is used to set the message that will be displayed to the user.
     * It notifies the observer (viewmodel - mvvm) so the message on screen will be updated.
     *
     * @param msg the message to be shown to the user.
     *
     */
    private void setMessage(String msg)
    {
        this.message = msg; // messages to the user and requests for input
        this.notifyObserver("message");
    }

    /**
     * The setName function sets the name of the guest player.
     *
     *
     * @param name is the name of the guest player
     *
     */
    private void setName(String name)
    {
        this.guest_player.setName(name);
    }

    /**
     * The getMySocket function returns the socket the guest player opened for communication with the games host.
     *
     *
     * @return The socket of the guest player
     */
    public Socket getMySocket() {

        return this.guest_player.socket;
    }

    /**
     * The setMySocket function sets the socket of the guest player to a given socket.
     *
     *
     * @param socket that will be set the socket of the guest player
     *
     */
    private void setMySocket(Socket socket) {

        this.guest_player.socket = socket;
    }

    /**
     * The decreaseScore function decreases the score of the guest player by a given number.
     *
     *
     * @param num Decrease the score of the player by that amount
     *
     */
    private void decreaseScore(int num)
    {
        this.guest_player.decreaseScore(num);
        this.notifyObserver("score");
    }

    /**
     * The addScore function adds the given number to the score of the guest player.
     *
     *
     * @param num Add the score to the guest player
     *
     */
    public void addScore(int num)
    {
        this.guest_player.addScore(num);
        this.notifyObserver("score");
    }

    /**
     * The notifyObserver function is used to notify all observers of the model that a change has occurred.
     *
     *
     * @param change Pass the change tag (so it knows what info wad changed) to the observer
     *
     */
    private void notifyObserver(String change) {
        setChanged();
        for(Observer obz: myObservers)
        {
            obz.update(this, change);
        }
    }
    /**
     * The addObserver function adds an observer to the list of observers.
     *
     *
     * @param obz is the observer that will be added to the list of observers
     *
     */
    @Override
    public void addObserver(Observer obz)
    {
        this.myObservers.add(obz);
    }

    /**
     * The addTiles function is called when the server sends a message that contains tiles for the guest player.
     * The function adds these tiles to the  player's strTiles arraylist, and notifies observers of this change.
     *
     * @param addedTiles is a string containing the received tiles from the server
     *
     */
    public void addTiles(String addedTiles) // received all 7 tiles
    {
        this.guest_player.strTiles.clear();
        String[] moreTiles = addedTiles.split("[.]");
        for(String s : moreTiles)
        {
            if(!s.equals("."))
                this.guest_player.strTiles.add(s);
        }
        this.notifyObserver("tiles");
        for(String s : guest_player.strTiles)
            System.out.println(s);
    }

    /**
     * The updateMatrixBoard function updates the board matrix with the new word after it was verified by the game server.
     *
     *
     * @param  word is the word that is being placed on the board
     * @param  row is the row of where the first letter of word needs to be placed
     * @param  col is the column of where the first letter of word needs to be placed
     * @param  vertical Determine whether the word is placed vertically or horizontally
     *
     */
    private void updateMatrixBoard(String word, String row, String col, String vertical) {
        if(vertical.equals("vertical"))
        {
            for(int i = 0; i < word.length(); i++)
            {
                if(word.charAt(i) != '_')
                {
                    this.board[Integer.parseInt(row) + i][Integer.parseInt(col)] = String.valueOf(word.charAt(i)) + "," + letterToScore.get(String.valueOf(word.charAt(i)));
                }
            }
        }
        else // "h"
        {
            for(int i = 0; i < word.length(); i++)
            {
                if(word.charAt(i) != '_')
                {
                    this.board[Integer.parseInt(row)][Integer.parseInt(col) + i] = String.valueOf(word.charAt(i)) + "," + letterToScore.get(String.valueOf(word.charAt(i)));
                }
            }
        }
        this.notifyObserver("board");
    }


    /**
     * The write_to_server function takes a string and a socket as input.
     * It then writes the string to the server through that socket.
     *
     * @param str id the message to the server
     * @param server_socket communicate (send) with the server through this socket
     *
     */
    private void write_to_server(String str, Socket server_socket){
        try {
            PrintWriter outToServer = new PrintWriter(server_socket.getOutputStream());
            outToServer.println(str);
            outToServer.flush();
            //outToServer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * The read_from_server function reads a line of text from the server.
     *
     *
     * @param server_socket (receive) communicate with the server through this socket
     *
     */
    private String read_from_server(Socket server_socket) {
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));
            String serverResponse = inFromServer.readLine();
            //inFromServer.close();
            return serverResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The connectToServer function is used to connect the guest player to the host server.
     *
     *
     * @param ip is the ip address of the server

     * @param port Connect to the server on a specific port
     *
     * @throw RuntimeException
     */
    private void connectToServer(String ip, int port){
        try {
            Socket hostServer =new Socket(ip,port);
            this.setMessage("Connection established successfully!");
            this.setMySocket(hostServer);
            this.startGuestGame();
        } catch (IOException e) {
            this.setMessage("connection to remote game failed");
            throw new RuntimeException(e);
        }
    }
    /**
     * The case0 function is called when the client receives a message from the server
     * that it has received 7 tiles. The function then calls notifyObserver to update
     * the GUI with this information, and sets a message for the user to see in their
     * game window. It also adds these tiles to its own list of tiles using addTiles().

     *
     * @param fromHost is a list containing the received tiles
     *
     */
    private void case0(String fromHost) // got 7 tiles
    {
        this.notifyObserver("name");
        this.setMessage("game started, you got seven tiles, wait for your turn");
        addTiles(fromHost);
    }

    /**
     * The serverWordResponse function is called when the client receives a response from the server
     * regarding whether or not a word was successfully placed on the board.
     * If the word was placed by other players, only the board is updated.
     * If it successfully placed by this user, then this function will update all of the necessary data
     * structures and notify observers that they need to be updated.
     * If the word was not found in the dictionary an offer for a challenge will be shown.
     *
     * @param fromHost Store the message received from the server
     *
     */
    private void serverWordResponse(String[] fromHost) //my word failed"2|true|0|null|name"
    {
        for(String s : fromHost)
            System.out.println(s);
        if(fromHost[1].equals("true") && !fromHost[4].equals(this.guest_player.name)) // other users word was placed on board
        {
            wordEnteredByOtherUser(fromHost);
            //this.notifyObserver("board");
            this.setMessage(fromHost[4] + " put a new word in the board");
        }

        else if(fromHost[1].equals("true") && fromHost[4].equals(this.guest_player.name)) // received:"2|true|score|a,1.b,2....|name|word(not full)|row|col|v\h"
        {
            wordEnteredByMe(fromHost);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.setMessage("turn over");
        }
        else // my query request returned false received:"2|false|null|null|name"
        {
            this.setMessage("challenge?");
        }
    }

    /**
     * The challengeResponse function is called when the client receives a challenge response from the server.
     * If the challenge was correct, then it will call on another function to handle that case.
     * Otherwise, if it was incorrect, then this function will deduct 10 points from your score and display a message saying so and end the player's turn.
     *
     * @param fromHost Store the message received from the server
     *
     */
    private void challengeResponse(String[] fromHost)
    {
        if(fromHost[1].equals("true"))
        {
            challengeTrue(fromHost);
        }
        else // challenge wasn't correct - deduce points received: "3|false|name"
        {
            this.setMessage("challenge returned false, you lose 10 points");
            this.decreaseScore(10);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.setMessage("turn over");
    }

    /**
     * The gameOver function is called when the game has ended.
     * It sets the message to be displayed on screen, and closes
     * the socket of the guest player.
     *
     * @param fromHost containing the winner of the game info
     *
     * @throws RuntimeException
     */
    private void gameOver(String fromHost)
    {
        this.setMessage(fromHost + ", Game Over"); // winner message
        this.gameRunning = false;

        try{
            this.guest_player.socket.close();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The wordEnteredByOtherUser function is called when the client receives a message from the server
     * indicating that another user has entered a word.
     * The function calls updateBoard to update the board matrix with the new word.
     *
     * @param  fromHost containing the information from the server
     *
     */
    private void wordEnteredByOtherUser(String[] fromHost)
    {
        updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
    }

    /**
     * The wordEnteredByMe function is called when the client receives a message from the server that indicates
     * the server sent a response for a requested word. The function checks if the word entered by them was valid and placed on
     * board or not. If it was, then they get points for it and their tiles are updated accordingly. If not, then
     * they do not get any points and their turn is over. In both cases, a message is displayed to indicate what happened.
     *
     * @param fromHost containing the information from the server
     *
     */
    private void wordEnteredByMe(String[] fromHost)
    {
        if(!fromHost[2].equals("0")) // my word was put into board
        {
            this.setMessage( "your word was placed on the board, you get " + fromHost[2] + " points");
            updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
            this.addScore(Integer.parseInt(fromHost[2]));

            this.addTiles(fromHost[3]); // add tiles received from the server replacing those used in the word add to board (received all of my tiles)
        }
        else //"2|true|0|name"
            this.setMessage("wrong word or placement, you get 0 points, turn over");
    }

    /**
     * The challengeTrue function is called when the client receives a message from the server that
     * indicates that a challenge was successful. The function updates the board and score accordingly,
     * and sets an appropriate message to be displayed to the user.
     *
     * @param fromHost containing the information from the server
     *
     */
    public void challengeTrue(String[] fromHost)
    {
        System.out.println("challenge true string:");
        for(String s : fromHost)
        {
            System.out.println(s);
        }
        if(!fromHost[2].equals("0")) // challenge and tryplaceword correct, received: "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
        {
            //int tmp_score = Integer.parseInt(fromHost[2]) + 10;
            this.setMessage( "your word was placed on the board, you get " + fromHost[2] + " points");
            updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
            this.addScore(Integer.parseInt(fromHost[2]));
            this.addTiles(fromHost[3]);
        }
        else // only challenge correct received: "3|true|0|name"
        {
            this.setMessage("challenge returned true but word couldn't be put into board. turn over");
        }
    }


    /**
     * The startGuestGame function is the main loop for the guest player.
     * It waits for a message from the host, and then performs an action based on that message.
     * The actions are:
     * 0 - receive seven tiles at start of game (case0)
     * 1 - it's my turn (setMessage)
     * 2 - server response to query request + updated board for any entered word (serverWordResponse)
     * 3 - server response to challenge request + updated board if challenge was successful or not(challengeResponse)
     * 4 - server notifying the game has ended and provides the winner's info (gameOver).
     *
     *
     */
    private void startGuestGame() {
        this.setMessage("please wait for Host to start the game");
        while (gameRunning){
            // reading from server
            String readfromHost = this.read_from_server(this.getMySocket());
            String[] fromHost = readfromHost.split("[|]");

            switch (fromHost[0]){
                case "0": // seven tiles at the start of the game
                    case0(fromHost[1]);
                    break;
                case "1": // my turn
                    this.setMessage("your turn");
                    break;//send: 1|word(not full)|row|col|v/h|name or 1|xxx|name

                case "2": // host + server response to query request ++ updated board for any entered word
                    System.out.println("guest model - entered query case");
                    serverWordResponse(fromHost);
                    break; // if user wants to challenge or not, send: 3|c/xxx|word(not full)|row|col|v/h|name

                case "3": // host + server response to challenge request
                    System.out.println("guest model - entered challenge case");
                    challengeResponse(fromHost);
                    break;

                case "4": // game over
                    gameOver(fromHost[1]);
                    break;
            }
        }
    }
}