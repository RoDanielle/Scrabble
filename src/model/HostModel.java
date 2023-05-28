/*
model: (in remote game: s = server(host) output to client. c = client output to server(host))
case 0: s- 0|a,5^d,8^l,1^......
        c - gets seven tiles at the beginning of the game sends nothing back

case 1: c - 1|word(not full)|row|col|v/h|name for the word it wants to enter or 1|xxx|name if wants to pass

case 2:s - there are three options for response: a - query and try... were good: "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                                  b - only query was good: "2|true|0|name"
                                                  c - query returned false: "2|false|name"
       c - if query false challenge option presented:for challenge sends  3|c|word(not full)|row|col|v/h|name for passing sends 3|xxx|word(not full)|row|col|v/h|name to host

       *** in case 2 we get from host words that were entered and put onto the board by other users in order to put into other players board and maintain an updated board for all

case 3: s - challenge request answer, again three options: a - challenge and try... were good: "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                                  b - only challenge was good: "3|true|0|name"
                                                  c - challenge returned false: "3|false|name"
 */

package model;

import server.BookScrabbleHandler;
import server.MyServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

public class HostModel implements GameModel {

    // data for host player
    String name;
    int score;
    String[][] board;
    List<String> tiles;

    // data for managing game
    List<Player> players;
    boolean gameRunning;
    Board boardObject;
    Tile.Bag bag;
    int numbOfPlayers;

    Socket gameServerSocket;

    int myPort;

    HostServer hs;

    public HostModel() {
        this.name = null;
        this.score = 0;
        this.board = new String[15][15];
        this.players = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.gameRunning = true;
        this.boardObject = Board.getBoard();
        this.bag = Tile.Bag.getBag();
        this.numbOfPlayers = 0;
        this.gameServerSocket = null;
        this.myPort = 8081;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public void addScore(int score) {
        this.score += score;
    }
    @Override
    public void decreaseScore(int score) {
        this.score -= score;
    }


    public String tileToString (Tile t) {
        String tileString  = null;
        tileString = valueOf(t.letter) + "," + Integer.toString(t.score);
        return tileString;
    }

    @Override
    // Getter method to access the matrix
    public String[][] getBoard() {
        return this.board;
    }

    public void stopGame() {
        this.gameRunning = false;
    }

    public void stopRemoteGame(){
        Player winner = new Player();
        for(Player p : this.players)
        {
            if(winner.getScore() < p.getScore())
            {
                winner = p;
            }
        }
        String win = "The winner is:" + winner.name + "with: " + winner.getScore() + "points";

        for(Player p : this.players)
        {
            String Gwin = "4|" + win;
            this.write_to_socket(Gwin, p.socket);
            if(p.socket != null)
            {
                this.write_to_socket(Gwin, p.socket);
            }
        }
        System.out.println(win);

        for (int i = 0; i < numbOfPlayers; i++) {
            try {
                if(players.get(i).socket != null)
                {
                    players.get(i).socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
    }

    public void GameManagement(){
        Scanner input = new Scanner(System.in);
        System.out.println("Do you want to play a local game or host a remote game? for local enter: 1, for remote enter: 2");
        String gameType = input.nextLine();

        if(gameType.equals("1"))
        {
            MyServer s=new MyServer(8080, new BookScrabbleHandler());
            s.start();
            System.out.println("You are now hosting a local game");
            startGame_local("localhost", 8080);
            s.close();
            //this.gameServerSocket.close(); // needed to be called only after startGame_local stopped running, meaning after the game is over.
        }
        else { //remote
            MyServer s=new MyServer(8080, new BookScrabbleHandler());
            s.start();
            this.hs = new HostServer();
            this.hs.start();
            //s.close();
        }
    }


    public void startGame_local(String gameServerIP, int gameServerPort) {
        Scanner input = new Scanner(System.in);
        System.out.println("How many players are expected to participate in this game? choose 1-4 (including you)");
        String num = input.nextLine();
        this.numbOfPlayers = Integer.parseInt(num);

        for(int i = 1; i <this.numbOfPlayers + 1; i++){
            System.out.println("please enter player " + i + " name:");
            String ans = input.nextLine();
            Player player = new Player();
            player.setName(ans);
            players.add(player);
        }

        System.out.println("starting game");
        for(Player player : players)
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
        for(Player player : players)
        {
            for(int i=0; i<7; i++)
            {
                Tile t = bag.getRand();
                player.tiles.add(t);
            }
        }

        while (gameRunning)
        {
            //System.out.println("connecting to game server...");
            try {
                ConnectToGameServer(gameServerIP,gameServerPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //System.out.println("you are connected to game server on port " + this.gameServerSocket);

            String player_request = null; // word
            String row = null;
            String col = null;
            String vertical = null;
            String fullWord = null;
            String in = null;

            String current_player = this.players.get(0).name;
            System.out.println(current_player + " turn");
            System.out.println("your tiles are: "); // will be changed when view is added
            for(Tile t :  this.players.get(0).tiles)
            {
                System.out.println(t.letter + "," + t.score);
            }
            printmatrix();
            System.out.println("please enter Word, if you don't have a word enter xxx");
            player_request = input.nextLine();
            if(!player_request.equals("xxx") && !player_request.equals("XXX")){
                System.out.println("Please enter row");
                row = input.nextLine();
                System.out.println("Please enter col");
                col = input.nextLine();
                System.out.println("Please enter v for vertical or h for horizontal");
                vertical = input.nextLine();
                fullWord = fill_spaces(player_request,row,col,vertical);
                String args = "Q,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullWord;
                //System.out.println(this.gameServerSocket.isClosed());
                this.write_to_socket(args, this.gameServerSocket); // sending query request to game server

                String server_response = this.read_from_socket(this.gameServerSocket); // response to query

                if(server_response.equals("true")) // word found in dictionary
                {
                    System.out.println("word was found in dictionary, trying to place on board");
                    Word w = players.get(0).create_word(player_request, row,col,vertical);
                    int score = this.boardObject.tryPlaceWord(w);
                    if(score != 0) // word was put into board
                    {
                        System.out.println(current_player + " got " + score + " points for the word");
                        players.get(0).addScore(score);
                        while(players.get(0).tiles.size() < 7) // fill missing tiles
                        {
                            if(this.bag.size() != 0)
                            {
                                players.get(0).tiles.add(this.bag.getRand());
                            }
                            else
                            {
                                this.stopGame();
                                break;
                            }
                        }
                        this.updateMatrixBoard(w); // updating matrix board
                    }
                    else
                    {
                        List<Tile> tmplst = new ArrayList<>();
                        tmplst = Arrays.stream(w.tiles).collect(Collectors.toList());
                        while(tmplst.size() > 0)
                        {
                            Tile t = tmplst.remove(0);
                            if(t != null)
                                players.get(0).tiles.add(t);   // give the player its tiles back
                        }
                        System.out.println(current_player + " your word could not be put into the board, you get 0 points");
                    }
                }
                else
                {
                    System.out.println("word not found in dictionary, want to challenge? enter 'c' for challenge else enter xxx");
                    in = input.nextLine();
                    if(in.equals("c") || in.equals("C"))
                    {
                        args = "C,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullWord;
                        this.write_to_socket(args, this.gameServerSocket); // sending challenge request to game server
                        server_response = this.read_from_socket(this.gameServerSocket); // response to challenge

                        if (server_response.equals("true"))
                        {
                            Word w = players.get(0).create_word(player_request, row,col,vertical);
                            int score = this.boardObject.tryPlaceWord(w);
                            if(score != 0) // word was put into board
                            {
                                System.out.println("challenge succeeded");
                                score+=10;
                                System.out.println(current_player + " got " + score + " point for the word");
                                players.get(0).addScore(score);
                                while(players.get(0).tiles.size() < 7) // fill missing tiles
                                {
                                    if(this.bag.size() != 0)
                                    {
                                        players.get(0).tiles.add(this.bag.getRand());
                                    }
                                    else{
                                        this.stopGame();
                                        break;
                                    }
                                }
                                this.updateMatrixBoard(w); // updating matrix board
                            }
                            else
                            {
                                List<Tile> tmplst = new ArrayList<>();
                                tmplst = Arrays.stream(w.tiles).toList();
                                while(tmplst.size() > 0)
                                {
                                    Tile t = tmplst.remove(0);
                                    if(t != null)
                                        players.get(0).tiles.add(t);   // give the player its tiles back
                                }
                                System.out.println("challenge succeeded but word couldn't be put into board");
                            }
                        }
                        else
                        {
                            System.out.println("challenge failed you lose 10 points");
                            players.get(0).decreaseScore(10);
                        }

                    }
                }
            }

            System.out.println("turn over");
            Player p = this.players.remove(0);
            this.players.add(p);
        }

        Player winner = new Player();
        for(Player p : this.players)
        {
            if(winner.getScore() < p.getScore())
            {
                winner = p;
            }
        }

        System.out.println("the winner is: " + winner.name + "with " + winner.getScore() + " points");
        System.out.println("Game Over");
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






