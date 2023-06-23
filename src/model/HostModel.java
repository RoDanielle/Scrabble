/*package model;

import server.BookScrabbleHandler;
import server.MyServer;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;
import static java.lang.String.valueOf;
*/
package model;

import server.BookScrabbleHandler;
import server.MyServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import static java.lang.String.valueOf;

public class HostModel extends Observable implements GameModel {
    // data for host player
    private List<Observer> myObservers;
    public Player hostPlayer;
    private String[][] board;
    Player winner;


    // data for managing game
    public boolean gameRunning;
    private Board boardObject;
    private Tile.Bag bag;
    private int numbOfPlayers;
    public Socket gameServerSocket;
    private String message;
    private Boolean isLocal;


    // data  for remote game
    private HostServer hs;
    private int myPort;


    // data for local game
    private List<Player> players;
    public Player current_player; // in local game will have a different player in every turn, in remote game will hold only the host.
    // with this data member we will get the necessary information to show in view each turn


    /**
     * The HostModel function is the constructor of the HostModel class.
     * The HostModel function will create a new thread that will run the game management function.
     *
     *
     * and then will wait for all players to connect before starting a new game. Once all players have connected, it will
     * send out tiles to each player until they have 7 tiles in their hand (the maximum number of tiles allowed). Then,
     * once every player has 7 tiles in their hand, it will start taking turns from each player until one wins or there are no more moves left on the board
     *
     * @param name Set the name of the host player
     * @param isLocal Determine whether the game is local or remote
     * @param numPlayers Determine how many players are playing the game
     *
     */
    public HostModel(String name, Boolean isLocal, int numPlayers) {
        this.isLocal = isLocal;
        this.hostPlayer = new Player();
        this.myObservers = new ArrayList<>();
        this.message = null;
        this.board = new String[15][15];
        this.players = new ArrayList<>();
        this.gameRunning = true;
        this.boardObject = Board.getBoard();
        this.bag = Tile.Bag.getBag();
        this.numbOfPlayers = numPlayers;
        this.gameServerSocket = null;
        this.myPort = 8081;
        this.winner = new Player();
        new Thread(()-> {
            this.GameManagement(isLocal, name);
        }).start();
    }

    /**
     * The getBoardObject function returns the boardObject variable.
     *
     * @return The boardobject variable
     */
    public Board getBoardObject() {
        return this.boardObject;
    }

    /**
     * The getGameRunning function returns the boolean value of gameRunning.
     *
     * @return The value of the gamerunning variable
     */
    public boolean getGameRunning() {
        return this.gameRunning;
    }

    /**
     * The getNumbOfPlayers function returns the number of players in the game.
     *
     * @return The number of players in the game
     */
    public int getNumbOfPlayers() {return this.numbOfPlayers;}

    /**
     * The getPlayers function returns a list of all players in the game.
     *
     * @return A list of players
     */
    public List<Player> getPlayers() {return this.players;}

    /**
     * The getBag function returns the bag of tiles that is used in the game the host player is hosting.
     *
     * @return The bag of the tile
     */
    public Tile.Bag getBag() {
        return this.bag;
    }
    /**
     * The getWinner function returns the winner of the game.
     *
     * @return The winner of the game
     */
    public Player getWinner() {return this.winner;}

    /**
     * The notifyObserver function is used to notify all observers of the model that a change has occurred.
     *
     * @param change Tells the observer what has changed
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
     * @param obz is added as an observer to the arraylist of observers
     *
     */
    @Override
    public void addObserver(Observer obz)
    {
        this.myObservers.add(obz);
    }

    /**
     * The decreaseScore function decreases the score of the current player by a given number.
     * It notifies a change in the score has occurred.
     *
     * @param num Decrease the score of the current player by num
     *
     */
    private void decreaseScore(int num)
    {
        this.current_player.decreaseScore(num);
        this.notifyObserver("score");
    }

    /**
     * The addScore function adds the given number to the current player's score.
     * It notifies a change in the score has occurred.
     *
     * @param num Add the provided num score to the current player
     *
     */
    private void addScore(int num)
    {
        this.current_player.addScore(num);
        this.notifyObserver("score");
    }

    /**
     * The getName function returns the name of the current player.
     * This function will be called in order to show the players name how is currently playing on the screen.
     *
     * @return The name of the current player
     */
    @Override
    public String getName(){ // for view
        return this.current_player.name;
    }

    /**
     * The getScore function returns the score of the current player.
     * This function will be called in order to get the score that needs to be shown on screen for the user.
     *
     * @return The score of the current player
     */
    @Override
    public int getScore() { // for view
        return this.current_player.getScore();
    }

    /**
     * The getBoard function returns the board matrix.
     * This function will be called in order to get the current board that needs to be shown on screen for the user.
     *
     * @return The 2d array of the board
     */
    @Override
    // Getter method to access the matrix
    public String[][] getBoard() { // for view
        return this.board;
    }

    /**
     * The getTiles function returns a list of strings that represent the tiles in the current player's hand.
     * This function will be called in order to present the tiles on screen.
     *
     *
     * @return A list of strings that represent the tiles
     */
    @Override
    public List<String> getTiles() {  // for view
        return this.current_player.strTiles;
    }

    /**
     * The getMessage function returns the message that will be shown on the screen.
     *
     * @return The message string
     */
    @Override
    public String getMessage() { // for view
        return this.message;
    }

    /**
     * The setMessage function sets the message to be displayed in the GUI.
     *
     * @param msg Set the message to be displayed
     *
     */
    public void setMessage(String msg)
    {
        this.message = msg; // messages to the user and requests for input
        this.notifyObserver("message");
    }

    /**
     * The updateMatrixBoard function updates the board matrix with a new word.
     * It notifies a change occurred in te board and sets a new message.
     *
     * @param w contains the information of the word that will be added into the board.
     *
     */
    public void updateMatrixBoard(Word w) {
        if(w.vertical)
        {
            for(int i = 0; i < w.tiles.length; i++)
            {
                if(w.tiles[i] != null)
                {
                    this.board[w.row + i][w.col] =  this.tileToString(w.tiles[i]);
                }
            }
        }
        else
        {
            for(int i = 0; i < w.tiles.length; i++)
            {
                if(w.tiles[i] != null)
                {
                    this.board[w.row][w.col + i] =  this.tileToString(w.tiles[i]);
                }
            }
        }
        this.notifyObserver("board");
        this.setMessage("a new word was put into the board");
    }

    /**
     * The tileToString function takes a Tile object and returns a string representation of the tile.
     *
     *
     * @param t holds the tiles info of the letter and score
     *
     * @return A string that has the letter and score of the tile separated by a comma
     */
    private String tileToString (Tile t) {
        String tileString  = null;
        tileString = valueOf(t.letter) + "," + Integer.toString(t.score);
        return tileString;
    }

    /**
     * The stopGame function sets the gameRunning variable to false, which will cause the game loop to stop running.
     *
     */
    public void stopGame() {
        this.gameRunning = false;
    }

    /**
     * The write_to_socket function takes a string and a socket as input.
     * It then writes the string to the socket.
     *
     * @param str is a message to the server
     * @param _socket Specify which socket the message should be sent to
     *
     */
    public void write_to_socket(String str, Socket _socket){
        try {
            PrintWriter outToServer = new PrintWriter(_socket.getOutputStream());
            outToServer.println(str);
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * The read_from_socket function reads a string from the socket and returns it.
     *
     * @param _socket Specify which socket to read from
     *
     * @return A string containing a message sent the given socket
     */
    public String read_from_socket(Socket _socket) {
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            String serverResponse = inFromServer.readLine();
            inFromServer.close();
            return serverResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The ConnectToGameServer function connects the host to a game server.
     * The host will di this in the name all players.
     *
     * @param gameServerIP represents the ip of the game server
     * @param gameServerPort Specify the port number of the game server
     *
     */
    public void ConnectToGameServer(String gameServerIP, int gameServerPort) throws IOException {
        Socket server;
        server = new Socket(gameServerIP,gameServerPort);
        this.gameServerSocket = server;

        //TODO - add a connection succeeded or failes message
    }

    /**
     * The GameServerAvailabilityCheck function checks to see if the game server is up and running.
     * If it is, then it sends a check message to the server and closes the socket.
     * If not, then an IOException will be thrown and caught by this function which will return false.
     * This will indicate to the hostModel to set up a new game server.
     *
     * @return A boolean value
     */
    private boolean GameServerAvailabilityCheck() {
        try (Socket s = new Socket("localhost", 8080)) {
            this.write_to_socket("check", s);
            s.close();
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        return false;
    }

    /**
     * The GameManagement function is responsible for setting up the game server if needed and starting the game.
     * If this host is hosting a remote player, then it will create a new hostServer to host the game on port 8081.
     * Otherwise, it will call startGame_local function that handles a local game.

     *
     * @param isLocal Determine whether the game is local or remote
     * @param names holds the names of the local players
     *
     */
    private void GameManagement(boolean isLocal, String names){
        if(!GameServerAvailabilityCheck()) // the game server is not up - this host will create it
        {
            MyServer s = new MyServer(8080, new BookScrabbleHandler());
            s.start();
        }

        if(isLocal)
        {
            this.setLocalPlayers(names);
            startGame_local("localhost", 8080);
        }
        else { //remote
            this.hostPlayer.setName(names);
            this.current_player = hostPlayer;

            this.hs = new HostServer(this);
            this.hs.start();
        }
    }

    /**
     * The giveTiles function is used to give tiles to the player.
     *
     * @param p Give the tiles to a specific player
     *
     */
    public void giveTiles(Player p)
    {
        while(p.tiles.size() < 7){
            if(bag.size() != 0)
            {
                Tile t = bag.getRand();
                p.tiles.add(t);
                p.strTiles.add(tileToString(t));
            }
            else
            {
                this.stopGame();
            }
        }
    }

    /**
     * The TryPutWordInBoard function is responsible for trying to place a word on the board.
     * It first creates a Word object from the given parameters, and then tries to place it on the board.
     * If the word was a part of a successful challenge request, we add a bonus of 10 points, and update all relevant data structures:
     * The player's score is updated by calling addScore(score), his tiles are removed by calling removeStrTiles(word) and he gets new tiles in return by calling giveTiles().
     * We also update our matrix representation of the board with updateMatrixBoard(), set an appropriate message for the view.
     * If the request failed the word tiles are returned to the current player.
     *
     * @param requestType Determine if the word was requested using a challenge or a query
     * @param word Create a word object
     * @param row Determine the row where the first letter of the word will be placed
     * @param col Determine the column of the first letter of a word
     * @param vertical Determine if the word is placed vertically or horizontally
     *
     */
    public void TryPutWordInBoard(String requestType, String word, String row, String col,String vertical){
        Word w = this.current_player.create_word(word, row,col,vertical);
        int score = this.boardObject.tryPlaceWord(w);
        if(score != 0) // word was put into board
        {
            if(requestType.equals("C"))
            {
                score+=10;
            }

            this.addScore(score);
            this.current_player.removeStrTiles(word);
            this.giveTiles(this.current_player); // fill missing tiles
            this.notifyObserver("tiles");
            this.updateMatrixBoard(w); // updating matrix board
            this.setMessage("you got " + score + " points, turn over");
        }
        else
        {
            this.returnTiles(w, this.current_player);
            this.setMessage("wrong word or placement, you get 0 points, turn over");
        }
    }

    /**
     * The setUserQueryInput function is called after the user has entered a word and its details in the GUI when its is turn.
     * The function checks if the word is valid, and if so, it adds it to the board and updated the players relevant information.
     * If not, it offers to challenge.
     *
     *
     * @param word Set the word that the player entered
     * @param row Stores the row number of the first letter of a word
     * @param col Stores the column number of the first letter of a word
     * @param vertical Determine whether the word is placed vertically or horizontally on the board
     *
     */
    @Override
    public void setUserQueryInput(String word, String row, String col, String vertical){
        this.current_player.wordDetails[0] = word;
        if(!word.equals("xxx") && !word.equals("XXX")){
            this.current_player.wordDetails[1]= row;
            this.current_player.wordDetails[2] = col;
            this.current_player.wordDetails[3] = vertical;

            try {
                ConnectToGameServer("localhost",8080);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(testDictionary("Q",this.current_player.wordDetails[0],this.current_player.wordDetails[1], this.current_player.wordDetails[2], this.current_player.wordDetails[3],this.gameServerSocket)) // word found in dictionary
            {
                this.TryPutWordInBoard("Q",this.current_player.wordDetails[0],this.current_player.wordDetails[1], this.current_player.wordDetails[2], this.current_player.wordDetails[3]);
            }
            else
            {
                this.setMessage("challenge?");  // offer to challenge
            }
        }
        else {
            this.current_player.wordDetails[1] = "null";
            this.current_player.wordDetails[2] = "null";
            this.current_player.wordDetails[3] = "null";
            this.setMessage("turn over");
        }
    }
    /**
     * The setUserChallengeInput function is used to set the user challenge input.
     * It calls other functions to check the challenge, if successful it tries to put the word in the board using TryPutWordInBoard
     * The function sets relevant messages for each result.
     *
     * @param request Determine whether the user wants to challenge or not
     *
     */
    @Override
    public void setUserChallengeInput(String request){
        if(request.equals("c") || request.equals("C"))
        {
            try {
                ConnectToGameServer("localhost",8080);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (testDictionary("C",this.current_player.wordDetails[0],this.current_player.wordDetails[1], this.current_player.wordDetails[2], this.current_player.wordDetails[3],this.gameServerSocket))
            {
                this.TryPutWordInBoard("C",this.current_player.wordDetails[0],this.current_player.wordDetails[1], this.current_player.wordDetails[2], this.current_player.wordDetails[3]);
            }
            else
            {
                this.setMessage("challenge failed you lose 10 points");
                this.decreaseScore(10);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.setMessage("turn over");
    }

    /**
     * The playerTurn function is responsible for giving the player tiles at its first turn,
     * notifying observers of the name, score and tiles of a player hoe is currently starting its turn.
     * It also sets a message to be displayed on screen.

     *
     * @param player Pass the player how's turn to play to the function
     *
     */
    public void playerTurn(Player player){
        giveTiles(player); // in first turn gives 7 tiles
        this.notifyObserver("name");
        this.notifyObserver("score");
        this.notifyObserver("tiles");
        this.setMessage(player.name + " turn");
    }

    /**
     * The testDictionary function takes in a requestType, word, row, col and vertical.
     * It then creates the appropriate string to send to the game server based on whether it is a query or challenge.
     * The function then writes this string to the socket and reads from it as well.
     * If true is returned by the server we return true otherwise false is returned by our function.

     *
     * @param  requestType Determine whether the client is sending a query or a challenge request to the server
     * @param word the requested word
     * @param row Determine the row of the first letter in a word
     * @param col Determine the column of the first letter in a word
     * @param vertical Determine if the word is being placed vertically or horizontally
     * @param gameSocket Pass the socket to communicate with the game server to the function
     *
     * @return A boolean value that indicates whether the word is in the dictionary
     */
    public boolean testDictionary(String requestType, String word, String row, String col, String vertical, Socket gameSocket)
    {
        String args = null;
        if(requestType.equals("Q"))
        {
            String fullWord = fill_spaces(word,row,col,vertical);
            args = "Q,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullWord;
        }
        else // "C"
        {
            String fullWord = fill_spaces(word,row,col,vertical);
            args = "C,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullWord;
        }

        this.write_to_socket(args, gameSocket);
        String server_response = this.read_from_socket(gameSocket);
        if(server_response.equals("true"))
        {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * The returnTiles function takes in a Word and a Player, and returns the tiles from the word to the player's tile rack.
     *
     *
     * @param word Access the tiles in the word
     * @param player Pass the player that will receive its tiles back
     *
     */
    private void returnTiles(Word word, Player player)
    {
        for(int i = 0; i < word.getTiles().length; i++)
        {
            Tile t =  word.getTiles()[i];
            if (t != null)
                player.tiles.add(t);
            word.getTiles()[i] = null;
        }
    }

    /**
     * The setTurns function is used to determine the order of turns for each player.
     * It does this by giving each player a random tile from the bag, and then sorting them in ascending order based on their tiles letter values.
     * The first tile that was given to each player is then returned back into the bag.
     *
     */
    private void setTurns()
    {
        for(Player player : this.players)
        {
            Tile t = bag.getRand();
            player.tiles.add(t);
        }

        players.sort(Comparator.comparingInt(a -> Character.getNumericValue(a.tiles.get(0).letter)));
        for(Player player : players)
        {
            Tile t= player.tiles.remove(0);
            bag.put(t);
        }
    }

    /**
     * The setLocalPlayers function takes a string of names separated by the pipe character (|) and splits them into an array.
     * The first name in the array is set as the host player's name, and then added to a list of players.
     * The remaining names are used to create new Player objects, which are also added to this list.
     *
     * @param names contains the names of all players in the game
     *
     */
    private void setLocalPlayers(String names)
    {
        String[] playersNames = names.split("[|]");
        this.hostPlayer.setName(playersNames[0]);
        this.current_player = hostPlayer;
        players.add(this.hostPlayer);
        for(int i = 1; i < playersNames.length; i++){
            Player player = new Player();
            player.setName(playersNames[i]);
            players.add(player);
        }
    }
    /**
     * The startGame_local function is the main function that runs the local mode game.
     * It starts by setting up a new game, and then it loops through all of the players in order to play their turns.
     * The loop stops when there are no more tiles left or if all players chose to pass their turn.

     *
     * @param gameServerIP is the ip of the game server
     * @param gameServerPort is the port the game server is listening on
     *
     */
    private void startGame_local(String gameServerIP, int gameServerPort){
        this.setMessage("starting game");
        this.setTurns();

        while (gameRunning)
        {
            this.current_player = this.players.get(0);

            Thread thread = new Thread(() -> {
                Boolean notOver = true;
                this.playerTurn(this.current_player);
                while(notOver)
                {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(this.getMessage().contains("turn over"))
                    {
                        notOver = false;
                    }
                }
            });
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.passesCountLocal(); // count turn passes in order to know if the game needs to be stopped
            Player p = this.players.remove(0);
            this.players.add(p);
        }
        this.stopLocalGame();
    }

    /**
     * The passesCountLocal function is a helper function that counts the number of passes in a round.
     * If all players pass, it calls stopGame function to end the game.
     *
     *
     */
    private void passesCountLocal(){
        int numOfPasses = 0;
        for(int i = 0; i < this.numbOfPlayers; i++)
        {
            if(this.players.get(i).wordDetails[0].equals("xxx"))
            {
                numOfPasses++;
            }
        }
        if(numOfPasses == this.numbOfPlayers)
        {
            this.stopGame();
        }
    }

    /**
     * The stopLocalGame function is called when a local game ends. It sets the winner to be whoever has the highest score, and then closes all of the sockets that were opened during gameplay.
     *
     */
    public void stopLocalGame()
    {
        this.winner = this.hostPlayer;
        for(Player p : this.players)
        {
            if(this.winner.getScore() < p.getScore())
            {
                this.winner = p;
            }
        }
        try {
            if(this.gameServerSocket != null)
            {
                if(!this.gameServerSocket.isClosed())
                    this.gameServerSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO - make these message in a popup screen
        this.setMessage("the winner is: " + this.winner.name + " with " + this.winner.getScore() + " points");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.setMessage("Game Over");
    }


    /**
     * The fill_spaces function takes in a word, row, column and vertical as parameters.
     * It then checks if the word contains an underscore character. If it does not contain an underscore character,
     * it returns the original word that was passed into the function. If there is an underscore character in the string,
     * we then iterate through each letter, iterated over the board replace it the missing letter according to the received parameters
     *
     * @param word Stores the word that is being checked
     * @param row Stores the row number of the first letter in a word
     * @param col Stores the column number of the first letter in a word
     * @param vertical Determine if the word is vertical or horizontal
     *
     * @return A string that is the word with spaces filled in
     */
    private String fill_spaces(String word, String row, String col, String vertical) {
        if (word.contains("_"))
        {
            char[] chars = word.toCharArray();
            for (int i = 0; i < word.length(); i++)
            {
                if (word.charAt(i) == '_')
                {

                    if (vertical.equals("vertical"))
                    {
                        chars[i] = this.boardObject.getTiles()[Integer.parseInt(row) + i][Integer.parseInt(col)].letter;
                    }
                    else
                        chars[i] = this.boardObject.getTiles()[Integer.parseInt(row)][Integer.parseInt(col)  + i].letter;
                }
            }
            String fullWord = new String(chars);
            return fullWord;
        }
        else
        {
            return word;
        }
    }
}