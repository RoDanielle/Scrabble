package model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.valueOf;

public class GuestHandler implements Runnable {

    public Board boardObject;
    public Tile.Bag bag;
    private Socket clientSocket;
    private boolean isClientTurn;
    private Socket gameServer;
    private BufferedReader inFromGameS;
    private PrintWriter outToGameS;

    private BufferedReader inFromClient;
    private PrintWriter outToClient;

    public Player p;
    private boolean gameRunning;

    public String[][] board;

    public GuestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.isClientTurn = false;
        this.p = new Player();
        this.gameRunning = true;
        this.boardObject = Board.getBoard();
        this.bag = Tile.Bag.getBag();
        this.board = new String[15][15];
    }

    public void ConnectToGameServer(String gameServerIP) throws IOException {
        System.out.println("guest (host) trying connect to game server");
        this.gameServer = new Socket(gameServerIP,8080);
        System.out.println("connected to game server from guest");
    }

    // Getter for isClientTurn
    public boolean isClientTurn() {
        return isClientTurn;
    }

    // Setter for isClientTurn
    public void setClientTurn(boolean isClientTurn) {
        this.isClientTurn = isClientTurn;
        System.out.println(p.name + " turn");
    }

    public void DisconnectFromGameServer()
    {
        try {
            inFromGameS.close();
            outToGameS.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String fill_spaces(String word, String row, String col, String vertical) {
        if (word.contains("_")) {
            char[] chars = word.toCharArray();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == '_') {

                    if (vertical.equals("V") || vertical.equals("v")) {
                        chars[i] = boardObject.getTiles()[Integer.parseInt(row) + i][Integer.parseInt(col)].letter;
                    } else
                        chars[i] = this.boardObject.getTiles()[Integer.parseInt(row)][Integer.parseInt(col) + i].letter;
                }
            }
            String fullWord = new String(chars);
            return fullWord;
        } else {
            return word;
        }
    }
    private String giveTiles()
    {
        String s_tiles = null;
        while(p.tiles.size() < 7){
            if(s_tiles == null)
            {
                Tile t = bag.getRand();
                p.tiles.add(t);
                s_tiles = this.tileToString(t);
            }
            else {
                Tile t = bag.getRand();
                p.tiles.add(t);
                s_tiles = s_tiles + "." + this.tileToString(t);
            }
        }
        return s_tiles;
    }
    private String tileToString (Tile t) {
        String tileString  = null;
        tileString = valueOf(t.letter) + "," + Integer.toString(t.score);
        return tileString;
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
            //inFromServer.close();
            return serverResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(){
        try {
            // Create input and output streams for the client socket
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToClient = new PrintWriter(clientSocket.getOutputStream(), true);

            String[] guestResponse = null;
            String toGuest = null;

            // sending seven tiles
            if(p.tiles.size() == 0)
            {
                toGuest = "0|" + giveTiles();
                outToClient.println(toGuest);
            }

            // Start the game loop
            //while (gameRunning) {
            // Check if it's the client's turn
            if (isClientTurn()) {
                System.out.println("guest turn");
                ConnectToGameServer("localhost");
                inFromGameS = new BufferedReader(new InputStreamReader(gameServer.getInputStream()));
                outToGameS = new PrintWriter(gameServer.getOutputStream(), true);
                // Client's turn logic

                toGuest = "1|your turn";
                write_to_socket(toGuest,this.clientSocket);

                guestResponse = read_from_socket(this.clientSocket).split("[|]");

                if (!guestResponse[1].equals("xxx") && !guestResponse[1].equals("XXX")) {
                    if (p.name == null) // first round
                    {
                        p.name = guestResponse[5];
                    }
                    String fullword = fill_spaces(guestResponse[1], guestResponse[2], guestResponse[3], guestResponse[4]);
                    String args = "Q,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullword;
                    outToGameS.println(args);
                    String server_response = inFromGameS.readLine();

                    if (server_response.equals("true")) // word found in dictionary
                    {
                        toGuest = "2" + "|" + "true";
                        Word w = p.create_word(guestResponse[1], guestResponse[2], guestResponse[3], guestResponse[4]);
                        int score = this.boardObject.tryPlaceWord(w);
                        toGuest = toGuest + "|" + score;
                        if (score != 0) // word was put into board
                        {
                            p.addScore(score);
                            toGuest = toGuest + "|" + giveTiles() + "|" + p.name + "|" + guestResponse[1] + "|" + guestResponse[2] + "|" + guestResponse[3] + "|" + guestResponse[4];
                            this.updateMatrixBoard(w); // updating matrix board
                            write_to_socket(toGuest,this.clientSocket);
                            // TODO - notify all players a word was put into board // "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                            // TODO - ADD STOP WHEN BAG IS EMPTY
                        }
                        else {
                            toGuest = toGuest + p.name; //"2|true|0|name"
                            write_to_socket(toGuest,this.clientSocket);

                            List<Tile> tmplst = new ArrayList<>();
                            tmplst = Arrays.stream(w.tiles).toList();
                            while (tmplst.size() > 0) {
                                Tile t = tmplst.remove(0);
                                if (t != null)
                                    p.tiles.add(t);   // give the player its tiles back
                            }
                        }
                    }
                    else
                    {
                        toGuest = "2" + "|" + "false" + p.name; //query return false //"2|false|name"
                        guestResponse = this.read_from_socket(this.clientSocket).split("[|]");
                        if (guestResponse[1].equals("c") || guestResponse[1].equals("C")) {
                            args = "C,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullword;
                            outToGameS.println(args);
                            server_response = inFromGameS.readLine();
                            if (server_response.equals("true")) //challenge return true
                            {
                                toGuest = "3" + "|true";
                                Word w = p.create_word(guestResponse[2], guestResponse[3], guestResponse[4], guestResponse[5]);
                                int score = this.boardObject.tryPlaceWord(w);
                                if (score != 0) // word was put into board
                                {
                                    score += 10;
                                    p.addScore(score); //bonus
                                    toGuest = toGuest + "|" + score + "|" + giveTiles() + "|" + p.name + "|" + guestResponse[1] + "|" + guestResponse[2] + "|" + guestResponse[3] + "|" + guestResponse[4];
                                    this.updateMatrixBoard(w); // updating host matrix board
                                    ///// TODO - notify all players a word was put into board // "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                } else {
                                    toGuest = toGuest + "|0|" + p.name; //"3|true|0|name" //challenge-V, try-X
                                    this.write_to_socket(toGuest,  this.clientSocket);
                                    List<Tile> tmplst = new ArrayList<>();
                                    tmplst = Arrays.stream(w.tiles).toList();
                                    while (tmplst.size() > 0) {
                                        Tile t = tmplst.remove(0);
                                        if (t != null)
                                            p.tiles.add(t);   // give the player its tiles back
                                    }
                                }
                            } else {
                                toGuest = "3" + "|false|" + p.name;
                                p.decreaseScore(10);
                                write_to_socket(toGuest, this.clientSocket);
                            }
                        }
                    }
                }
                // Set the flag to indicate the server's turn is over
                isClientTurn = false;
                System.out.println("isClientTurn: " + isClientTurn);
            }

        } catch(IOException e){
            e.printStackTrace();
        }
        //DisconnectFromGameServer();
    }

}
