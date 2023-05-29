package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.String.valueOf;

public class HostServer {
    private List<GuestHandler> clientHandlers;
    private boolean isServerTurn;

    private Player hostp;

    String[][] board;

    // data for managing game
    boolean gameRunning;
    Board boardObject;
    Tile.Bag bag;
    Socket gameServerSocket;

    int myPort;

    boolean stop;

    public HostServer() {
        this.clientHandlers = new ArrayList<>();
        this.isServerTurn = false; // Initially, it's the client's turn
        this.hostp = new Player();
        this.stop = true;
        this.boardObject = Board.getBoard();
        this.bag = Tile.Bag.getBag();
        this.board = new String[15][15];
    }

    public void start(){
        this.stop = false;
        new Thread(()->startServer(8081)).start();
    }

    public void startServer(int port) {
        try {
            // Create a server socket to listen for client connections
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);
            Scanner input = new Scanner(System.in);
            System.out.println("How many players are expected to participate in this game? choose 1-4 (including you)");
            String num = input.nextLine();
            System.out.println("You are now hosting a remote game");
            System.out.println("please enter your name:");
            String ans = input.nextLine();
            this.hostp.setName(ans);

            // Start accepting client connections
            while (!stop) {
                System.out.println("Waiting for remote players.....");
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new ClientHandler for the client and add it to the list
                GuestHandler clientHandler = new GuestHandler(clientSocket);
                clientHandlers.add(clientHandler);

                // Start a new thread to handle the client
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

                // Start the game when the all client connects
                if (clientHandlers.size() == Integer.parseInt(num) - 1 || (clientHandlers == null && Integer.parseInt(num) == 0)) {
                    startGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket ConnectToGameServer(String gameServerIP, int gameServerPort) throws IOException {
        Socket server;
        server = new Socket(gameServerIP,gameServerPort);
        return server;
    }
    private void startGame() {
        // Set the flag to indicate the client's turn
        isServerTurn = true;
        int Guestturns = 0;
        // Example of changing turns
        while (true) {
            // Check if it's the server's turn
            if (isServerTurn) {
                System.out.println("Host turn, trying to connect to game server");
                Socket gameServer = null;
                try {
                    gameServer = ConnectToGameServer("localhost",8080);
                    System.out.println("Host connected to game server");
                    hostTurn(gameServer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Set the flag to indicate the client's turn
                isServerTurn = false;
            }
            else {
                // Client's turn logic
                System.out.println("clientHandlers size is: " + clientHandlers.size());
                clientHandlers.get(0).setClientTurn(true);
                clientHandlers.get(0).run();
                //clientHandlers.get(0).setClientTurn(true);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread());
                clientHandlers.get(0).setClientTurn(false);
                System.out.println("GUEST TURN OVER");
                isServerTurn = true;

                /* // USE FOR MORE THAN ONE REMOTE PLAYER
                clientHandlers.get(Guestturns).setClientTurn(true);
                Guestturns++;
                // Set the flag to indicate the server's turn
                if(Guestturns == clientHandlers.size() - 1)
                {
                    isServerTurn = true;
                    Guestturns = 0;
                }
                 */
            }
        }
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
            //System.out.println("close? " + _socket.isClosed());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            String serverResponse = inFromServer.readLine();
            inFromServer.close();
            return serverResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hostTurn(Socket server){
        String player_request = null; // word
        String row = null;
        String col = null;
        String vertical = null;
        String fullWord = null;

        String current_player = this.hostp.name;
        System.out.println(current_player + " turn");
        while(this.hostp.tiles.size() < 7)
        {
            if(this.bag.size() != 0)
            {
                this.hostp.tiles.add(this.bag.getRand());
            }
        }
        printboard();
        System.out.println("your tiles are: ");
        for(Tile t : this.hostp.tiles)
        {
            System.out.println(t.letter + "," + t.score);
        }
        System.out.println("please enter Word, if you don't have a word enter xxx");
        Scanner input = new Scanner(System.in);
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

            this.write_to_socket(args, server); // sending query request to game server
            System.out.println("sent query request to game server:" + args);
            String server_response = this.read_from_socket(server); // response to query

            if(server_response.equals("true")) // word found in dictionary
            {
                System.out.println("word found in dictionary, trying to place in board");
                Word w = this.hostp.create_word(player_request, row,col,vertical);
                int score = this.boardObject.tryPlaceWord(w);
                if(score != 0) // word was put into board
                {
                    System.out.println(current_player + " got " + score + " point for the word");
                    this.hostp.addScore(score);
                    while(this.hostp.tiles.size() < 7) // fill missing tiles
                    {
                        if(this.bag.size() != 0)
                        {
                            this.hostp.tiles.add(this.bag.getRand());
                        }
                        else {
                            //this.stopGame();
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
                            this.hostp.tiles.add(t);   // give the player its tiles back
                    }

                    System.out.println(current_player + " your word could not be put into the board, you get 0 points");
                }
            }
            else
            {
                System.out.println("word not found in dictionary, want to challenge? enter 'c' for challenge else enter xxx");
                String in = input.nextLine();
                if(in.equals("c") || in.equals("C"))
                {
                    args = "C,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullWord;
                    this.write_to_socket(args, server); // sending challenge request to game server
                    server_response = this.read_from_socket(server); // response to challenge

                    if (server_response.equals("true"))
                    {
                        Word w =this.hostp.create_word(player_request, row,col,vertical);
                        int score = this.boardObject.tryPlaceWord(w);
                        if(score != 0) // word was put into board
                        {
                            System.out.println("challenge succeeded");

                            score+=10;
                            System.out.println(current_player + " got " + score + " point for the word");
                            this.hostp.addScore(score);
                            while(this.hostp.tiles.size() < 7) // fill missing tiles
                            {
                                if(this.bag.size() != 0)
                                {
                                    this.hostp.tiles.add(this.bag.getRand());
                                }
                                else {
                                    //this.stopGame();
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
                                    this.hostp.tiles.add(t);   // give the player its tiles back
                            }
                            System.out.println("challenge succeeded but word couldn't be put into board");
                        }
                    }
                    else
                    {
                        System.out.println("challenge failed you lose 10 points");
                        this.hostp.decreaseScore(10);
                    }

                }
            }
        }

        System.out.println("turn over");
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
        printboard();
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
    public String tileToString (Tile t) {
        String tileString  = null;
        tileString = valueOf(t.letter) + "," + Integer.toString(t.score);
        return tileString;
    }
}