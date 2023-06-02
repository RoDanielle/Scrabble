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

import viewModel.ViewModel;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class GuestModel extends Observable implements GameModel{

    private Player guest_player;
    private String[][] board;
    //private List<String> tiles;
    private Map<String,String> letterToScore;
    private String request_to_server;
    private String message;

    boolean gameRunning;

    private ViewModel myObserver;

    public GuestModel(String name, String ip, int port, ViewModel vmObserver){
        this.myObserver = vmObserver;
        this.guest_player = new Player();
        this.setName(name);
        this.board = new String[15][15];
        //this.tiles = new ArrayList<>();
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

    @Override
    public String getName() {
        return this.guest_player.name;
    }

    @Override
    public int getScore() {
        return this.guest_player.getScore();
    }

    @Override
    public List<String> getTiles() {
        return this.guest_player.strTiles;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
    @Override
    public String[][] getBoard() {
        return this.board;
    }

    public void setMessage(String msg)
    {
        this.message = msg;
        this.notifyObserver("message");
    }

    public void setName(String name)
    {
        this.guest_player.setName(name);
        this.notifyObserver("name");
    }

    public Socket getMySocket() {

        return this.guest_player.socket;
    }

    public void setMySocket(Socket socket) {

        this.guest_player.socket = socket;
    }

    public void decreaseScore(int num)
    {
        this.guest_player.decreaseScore(num);
        this.notifyObserver("score");
    }

    public void addScore(int num)
    {
        this.guest_player.addScore(num);
        this.notifyObserver("score");
    }

    public void notifyObserver(String change) {
        setChanged();
        this.myObserver.update(this, change);
    }

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
    }

    public void updateMatrixBoard(String word, String row, String col, String vertical) {
        String tmpTileString = null;
        if(vertical.equals("v") || vertical.equals("V"))
        {
            for(int i = 0; i < word.length(); i++)
            {
                if(word.charAt(i) != '_')
                {
                    tmpTileString = word.charAt(i) + "," + this.letterToScore.get(String.valueOf(word.charAt(i)));
                    this.board[Integer.parseInt(row) + i][Integer.parseInt(col)] = tmpTileString;
                }
            }
        }
        else // "h"
        {
            for(int i = 0; i < word.length(); i++)
            {
                if(word.charAt(i) != '_')
                {
                    tmpTileString = word.charAt(i) + "," + this.letterToScore.get(String.valueOf(word.charAt(i)));
                    this.board[Integer.parseInt(row)][Integer.parseInt(col) + i] = tmpTileString;
                }
            }
        }
        this.notifyObserver("board");
    }


    public void write_to_server(String str, Socket server_socket){
        try {
            PrintWriter outToServer = new PrintWriter(server_socket.getOutputStream());
            outToServer.println(str);
            outToServer.flush();
            //outToServer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String read_from_server(Socket server_socket) {
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));
            String serverResponse = inFromServer.readLine();
            //inFromServer.close();
            return serverResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectToServer(String ip, int port){
        try {
            Socket hostServer =new Socket(ip,port);
            this.setMessage("Connection established successfully!");
            this.setMySocket(hostServer);
            this.startGuestGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void case0(String fromHost) // got 7 tiles
    {
        this.setMessage("game started, you got seven tiles, wait for your turn");
        addTiles(fromHost);
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


    public String wordInput()
    {
        String[] wordDetails = askWord();
        request_to_server = "1|" + wordDetails[0] + "|" + wordDetails[1] + "|" + wordDetails[2] + "|" + wordDetails[3] + this.getName();
        return request_to_server;
    }

    public void serverWordResponse(String[] fromHost)
    {
        if(fromHost[1].equals("true") && !fromHost[4].equals(this.guest_player.name)) // other users word was placed on board
        {
            wordEnteredByOtherUser(fromHost);
            this.setMessage(fromHost[4] + "put a new word in the board");
        }

        else if(fromHost[1].equals("true") && fromHost[4].equals(this.guest_player.name)) // received:"2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
        {
            wordEnteredByMe(fromHost);
            this.setMessage("turn over");
        }
        else // my query request returned false received:"2|false|name"
        {
            write_to_server(queryFalse(),this.getMySocket()); // request challenge or not
        }
    }

    public void challengeResponse(String[] fromHost)
    {
        if(fromHost[1].equals("true"))
        {
            challengeTrue(fromHost);
        }
        else // challenge wasn't correct - deduce points received: "3|false|name"
        {
            System.out.println("challenge returned false, you lose 10 points");
            this.decreaseScore(10);
        }
        this.setMessage("turn over");
    }

    public void gameOver(String fromHost)
    {
        this.setMessage(fromHost); // winner message
        this.setMessage("Game Over");
        try{
            this.guest_player.socket.close();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void wordEnteredByOtherUser(String[] fromHost)
    {
        updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
    }

    public void wordEnteredByMe(String[] fromHost)
    {
        if(!fromHost[2].equals("0")) // my word was put into board
        {
            this.setMessage( "your word was placed on the board, you get " + fromHost[2] + " points");
            updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
            this.addScore(Integer.parseInt(fromHost[2]));

            //this.guest_player.removeStrTiles(fromHost[5]); // remove the tiles used in the word that was put into the board -- guesthandler handels this part
            this.addTiles(fromHost[3]); // add tiles received from the server replacing those used in the word add to board (recieved all of my tiles)
        }
        else //"2|true|0|name"
            this.setMessage("your word couldn't be fit into the board");
    }

    public String queryFalse()
    {
        Scanner input = new Scanner(System.in);
        this.setMessage("couldn't find your word in dictionary");
        this.setMessage("for challenge enter c for passing your turn enter xxx");
        String in = input.nextLine();
        String[] temp_request = this.request_to_server.split("[|]");
        this.request_to_server = "3" + "|" + in + "|" + temp_request[1] + temp_request[2] + temp_request[3] + temp_request[4] +temp_request[5];
        if(in.equals("XXX") || in.equals("xxx"))
        {
            this.setMessage("turn over");
        }
        return request_to_server;
    }
    public void challengeTrue(String[] fromHost)
    {
        if(!fromHost[2].equals("0")) // challenge and tryplaceword correct, received: "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
        {
            int tmp_score = Integer.parseInt(fromHost[2]) + 10;
            this.setMessage( "your word was placed on the board, you get " + tmp_score + " points");
            updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
            this.addScore(tmp_score);
            //this.guest_player.removeStrTiles(fromHost[5]);
            this.addTiles(fromHost[3]);
        }
        else // only challenge correct received: "3|true|0|name"
        {
            this.setMessage("challenge returned true but word couldn't be put into board. turn over");
        }
    }

    public void printboard() {
        System.out.println("the board is: ");
        System.out.print("  ");
        for (int k = 0; k < 15; k++) {
            System.out.print(" " + k + " ");
        }
        System.out.println("");
        for (int i = 0; i < 15; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 15; j++) {
                if (this.board[i][j] != null) {
                    if (j < 11) {
                        System.out.print(" " + this.board[i][j] + " ");
                    } else {
                        System.out.print("  " + this.board[i][j] + " ");
                    }

                } else {
                    if (j < 11) {
                        System.out.print(" _ ");
                    } else {
                        System.out.print("  _ ");
                    }
                }
            }
            System.out.println("");
        }
    }

    public void startGuestGame(){
        Scanner input = new Scanner(System.in);
        String request_to_server = null;
        this.setMessage("please wait for Host to start the game");

        while (gameRunning){
            // reading from server
            String readfromHost = this.read_from_server(this.getMySocket());
            System.out.println(readfromHost);
            String[] fromHost = readfromHost.split("[|]");
            switch (fromHost[0]){
                case "0": // seven tiles at the start of the game
                    case0(fromHost[1]);
                    break;
                case "1": // my turn
                    printboard();
                    this.request_to_server = wordInput();
                    write_to_server(this.request_to_server,this.getMySocket());
                    break;//send: 1|word(not full)|row|col|v/h|name or 1|xxx|name

                case "2": // host + server response to query request ++ updated board for any entered word
                    serverWordResponse(fromHost);
                    break; // if user wants to challenge or not, send: 3|c/xxx|word(not full)|row|col|v/h|name

                case "3": // host + server response to challenge request
                    challengeResponse(fromHost);
                    break;

                case "4": // game over
                    gameOver(fromHost[1]);
                    break;
            }
        }
    }
}
