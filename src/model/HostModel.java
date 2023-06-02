package model;

import server.BookScrabbleHandler;
import server.MyServer;
import viewModel.ViewModel;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

public class HostModel extends Observable implements GameModel {

    // data for host player
    private ViewModel myObserver;
    public Player hostPlayer;
    private String[][] board;
    //private List<String> tiles;

    // data for managing game

    boolean gameRunning;
    private Board boardObject;
    private Tile.Bag bag;
    int numbOfPlayers;
    Socket gameServerSocket;
    String message;

    Boolean isLocal;

    // data  for remote game
    private HostServer hs;
    int myPort;

    // data for local game
    private List<Player> players;

    public Player current_player; // in local game will have a different player in every turn, in remote game will hold only the host.
    // with this data member we will get the necessary information to show in view each turn


    public HostModel(String name, Boolean isLocal, int numPlayers,ViewModel vmObserver) {
        this.isLocal = isLocal;
        this.hostPlayer = new Player();
        this.hostPlayer.setName(name);
        this.current_player = hostPlayer;
        this.myObserver = vmObserver;
        this.message = null;
        this.board = new String[15][15];
        this.players = new ArrayList<>();
        this.gameRunning = true;
        this.boardObject = Board.getBoard();
        this.bag = Tile.Bag.getBag();
        this.numbOfPlayers = numPlayers;
        this.gameServerSocket = null;
        this.myPort = 8081;
        new Thread(()-> {
            this.GameManagement(isLocal);
        }).start();
    }

    public Board getBoardObject() {
        return this.boardObject;
    }

    public Tile.Bag getBag() {
        return this.bag;
    }

    public void notifyObserver(String change) {
        setChanged();
        this.myObserver.update(this, change);
    }

    public void decreaseScore(int num)
    {
        this.current_player.decreaseScore(num);
        this.notifyObserver("score");
    }

    public void addScore(int num)
    {
        this.current_player.addScore(num);
        this.notifyObserver("score");
    }

    @Override
    public String getName(){ // for view
        return this.current_player.name;
    }

    @Override
    public int getScore() { // for view
        return this.current_player.getScore();
    }

    @Override
    // Getter method to access the matrix
    public String[][] getBoard() { // for view
        return this.board;
    }

    @Override
    public List<String> getTiles() {  // for view
        return this.current_player.strTiles;
    }

    @Override
    public String getMessage() { // for view
        return this.message;
    }

    public void setMessage(String msg)
    {
        this.message = msg; // messages to the user and requests for input
        this.notifyObserver("message");
    }

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
    }

    public String tileToString (Tile t) {
        String tileString  = null;
        tileString = valueOf(t.letter) + "," + Integer.toString(t.score);
        return tileString;
    }


    public void stopGame() {
        this.gameRunning = false;
    }

    public void printmatrix() // will move to view later on
    {
        System.out.println("the board is: ");
        System.out.print("  ");
        for(int k = 0; k < 15; k++)
        {
            System.out.print(" " + k + " ");
        }
        System.out.println("");
        for(int i = 0; i < 15; i++)
        {
            System.out.print( i + " " );
            for(int j = 0; j < 15; j++)
            {
                if(this.boardObject.TilesBoard[i][j] != null)
                {
                    if(j < 11)
                    {
                        System.out.print(" " + this.boardObject.TilesBoard[i][j].letter + " ");
                    }
                    else
                    {
                        System.out.print("  " + this.boardObject.TilesBoard[i][j].letter + " ");
                    }

                }
                else {
                    if(j < 11)
                    {
                        System.out.print(" _ ");
                    }
                    else
                    {
                        System.out.print("  _ ");
                    }
                }
            }
            System.out.println("");
        }
    }

    public void write_to_socket(String str, Socket _socket){
        try {
            PrintWriter outToServer = new PrintWriter(_socket.getOutputStream());
            outToServer.println(str);
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
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

    public void ConnectToGameServer(String gameServerIP, int gameServerPort) throws IOException {
        Socket server;
        server = new Socket(gameServerIP,gameServerPort);
        this.gameServerSocket = server;

        //TODO - add a connection succeeded or failes message
    }

    public void GameManagement(boolean isLocal){
        if(isLocal)
        {
            MyServer s=new MyServer(8080, new BookScrabbleHandler());
            s.start();
            this.setMessage("You are now hosting a local game");
            startGame_local("localhost", 8080);
            s.close();
            //this.gameServerSocket.close(); // needed to be called only after startGame_local stopped running, meaning after the game is over.
        }
        else { //remote
            MyServer s=new MyServer(8080, new BookScrabbleHandler());
            s.start();
            this.hs = new HostServer(this);
            this.hs.start();
            //s.close();
        }
    }

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

    public String[] askWord()
    { // might ask in view for all the detail and have viewmodel pass the in a String[]
        Scanner input = new Scanner(System.in); // TODO delete and replace with input from view
        String[] wordDetails = new String[4];
        this.setMessage("please enter Word, if you don't have a word enter xxx");
        wordDetails[0] = input.nextLine();
        if(! wordDetails[0].equals("xxx") && ! wordDetails[0].equals("XXX")) {
            this.setMessage("Please enter row");
            wordDetails[1] = input.nextLine();
            this.setMessage("Please enter col");
            wordDetails[2] = input.nextLine();
            this.setMessage("Please enter v for vertical or h for horizontal");
            wordDetails[3] = input.nextLine();
        }
        else {
            wordDetails[1] = "null";
            wordDetails[2] = "null";
            wordDetails[3] = "null";
        }
        return wordDetails;
    }

    public boolean wantChallenge()
    {
        Scanner input = new Scanner(System.in); // TODO delete both and replace with input from view
        String in;


        this.setMessage("word not found in dictionary, want to challenge? enter 'c' for challenge else enter xxx");
        in = input.nextLine();
        if(in.equals("c") || in.equals("C"))
            return true;
        else
            return false;
    }

    public void TryPutWordInBoard(String requestType, String word, String row, String col,String vertical){
        Word w = this.current_player.create_word(word, row,col,vertical);
        int score = this.boardObject.tryPlaceWord(w);
        this.setMessage("word was found in dictionary, trying to place on board");
        if(score != 0) // word was put into board
        {
            if(requestType.equals("C"))
            {
                score+=10;
            }

            this.setMessage("you got " + score + " points for the word");
            this.addScore(score);
            this.current_player.removeStrTiles(word);
            this.giveTiles(this.current_player); // fill missing tiles
            this.notifyObserver("tiles");
            this.updateMatrixBoard(w); // updating matrix board
        }
        else
        {
            this.returnTiles(w, this.current_player);
            this.setMessage("your word could not be put into the board, you get 0 points");
        }
    }


    public void playerTurn(Socket gameSocket, Player player){
        giveTiles(player); // in first turn gives 7 tiles
        this.notifyObserver("name");
        this.notifyObserver("score");
        this.notifyObserver("tiles");
        this.setMessage(player.name + " turn");

        printmatrix(); // -----------------delete after view is set

        String[] userWordDetails = this.askWord();
        if(!userWordDetails[0].equals("xxx") && !userWordDetails[0].equals("XXX"))
        {
            String player_request = userWordDetails[0]; // word
            String row = userWordDetails[1];
            String col = userWordDetails[2];
            String vertical = userWordDetails[3];

            if(testDictionary("Q",player_request,row,col,vertical,this.gameServerSocket)) // word found in dictionary
            {
                this.TryPutWordInBoard("Q",player_request,row,col,vertical);
            }
            else
            {
                if(this.wantChallenge())
                {
                    if (testDictionary("C",player_request,row,col,vertical,this.gameServerSocket))
                    {
                        this.TryPutWordInBoard("C",player_request,row,col,vertical);
                    }
                    else
                    {
                        this.setMessage("challenge failed you lose 10 points");
                        this.decreaseScore(10);
                    }
                }
            }
        }
        this.setMessage("turn over");
    }

    boolean testDictionary(String requestType, String word, String row, String col, String vertical, Socket gameSocket)
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

    public void returnTiles(Word word, Player player)
    {
        //List<Tile> tmplst = new ArrayList<>();
        List<Tile> tmplst = Arrays.stream(word.tiles).collect(Collectors.toList());
        while(tmplst.size() > 0)
        {
            Tile t = tmplst.remove(0);
            if(t != null)
                player.tiles.add(t);   // give the player its tiles back
        }
    }

    public void setTurns()
    {
        for(Player player : this.players)
        {
            Tile t = bag.getRand();
            player.tiles.add(t);
        }

        players.sort((a,b)->Character.getNumericValue(a.tiles.get(0).letter) - Character.getNumericValue(b.tiles.get(0).letter));
        for(Player player : players)
        {
            Tile t= player.tiles.remove(0);
            bag.put(t);
        }
    }
    public void startGame_local(String gameServerIP, int gameServerPort) {
        Scanner input = new Scanner(System.in);

        players.add(this.hostPlayer);
        for(int i = 2; i <this.numbOfPlayers + 1; i++){
            this.setMessage("please enter player " + i + " name:");
            String ans = input.nextLine(); // here need to get names from view+viewmodel
            Player player = new Player();
            player.setName(ans);
            players.add(player);
        }

        this.setMessage("starting game");
        this.setTurns();

        while (gameRunning)
        {
            this.current_player = this.players.get(0);

            //System.out.println("connecting to game server...");
            try {
                ConnectToGameServer(gameServerIP,gameServerPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            playerTurn(this.gameServerSocket, this.current_player);

            Player p = this.players.remove(0);
            this.players.add(p);
        }

        this.stopLocalGame();
    }

    private void stopLocalGame()
    {
        Player winner = new Player();
        for(Player p : this.players)
        {
            if(winner.getScore() < p.getScore())
            {
                winner = p;
            }
        }
        try {
            this.gameServerSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setMessage("the winner is: " + winner.name + "with " + winner.getScore() + " points");
        this.setMessage("Game Over");
    }


    public String fill_spaces(String word, String row, String col, String vertical) {
        if (word.contains("_"))
        {
            char[] chars = word.toCharArray();
            for (int i = 0; i < word.length(); i++)
            {
                if (word.charAt(i) == '_')
                {

                    if (vertical.equals("V") ||  vertical.equals("v"))
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
