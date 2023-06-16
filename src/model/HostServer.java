package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HostServer {
    private List<GuestHandler> clientHandlers;
    private boolean isHostTurn;
    boolean gameRunning;
    boolean stop;
    private HostModel hostModel;

    public HostServer(HostModel host) {
        this.hostModel = host;
        this.clientHandlers = new ArrayList<>();
        this.isHostTurn = false; // Initially, it's the client's turn
        this.stop = true;
        this.gameRunning = host.gameRunning;
    }

    public void start(){
        this.stop = false;
        new Thread(()->startServer(8081)).start();
    }

    public void startServer(int port) {
        try {
            Scanner input = new Scanner(System.in);

            // Create a server socket to listen for client connections
            ServerSocket serverSocket = new ServerSocket(port);
            hostModel.setMessage("Server started. Listening on ip 'loclhost' and port " + port);
            hostModel.setMessage("You are now hosting a remote game");

            // Start accepting client connections
            while (!stop) {
                hostModel.setMessage("Waiting for remote players.....");
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();
                hostModel.setMessage("New client connected: " + clientSocket);

                // Create a new ClientHandler for the client and add it to the list
                GuestHandler clientHandler = new GuestHandler(clientSocket, this.hostModel);
                clientHandlers.add(clientHandler);

                // Start a new thread to handle the client
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

                // Start the game when the all client connects
                if (clientHandlers.size() == this.hostModel.numbOfPlayers - 1) {
                    startGame();
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        // Set the flag to indicate the client's turn
        isHostTurn = true;
        int Guestturns = 0;

        while (this.hostModel.gameRunning) {
            Socket gameServer = null;
           // try {
                //this.hostModel.ConnectToGameServer("localhost",8080); // connect to game server for each turn
                //gameServer = this.hostModel.gameServerSocket;
                // Check if it's the server's turn
                if (isHostTurn) {
                    int hostScore = this.hostModel.getScore();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Thread thread = new Thread(() -> {
                        Boolean notOver = true;
                        this.hostModel.playerTurn(this.hostModel.current_player);
                        while(notOver)
                        {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if(this.hostModel.getMessage().contains("turn over"))
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

                    if(hostScore != this.hostModel.getScore()) // host added a word into the board
                    {
                        notifyRemotes(this.hostModel.current_player.wordDetails, this.hostModel.current_player.name);
                    }

                    // Set the flag to indicate the client's turn
                    isHostTurn = false;
                }
                else {

                    // Client's turn logic
                    clientHandlers.get(0).setClientTurn(true);

                    Thread guestThread = new Thread(() -> {

                        clientHandlers.get(0).run();
                    });
                    guestThread.start();

                    try {
                        guestThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    clientHandlers.get(0).setClientTurn(false);
                    Guestturns +=1; // a guest played its turn

                    // notify all other remote players and the host the board was changed
                    if(!clientHandlers.get(0).addedWordStr.equals("null")) // user put a word in the board
                    {
                        notifyBoardChanged(clientHandlers.get(0).addedWordStr, clientHandlers.get(0).addedWord);
                        clientHandlers.get(0).addedWord = null;
                        clientHandlers.get(0).addedWordStr = "null";
                    }

                    GuestHandler tmp = clientHandlers.remove(0);
                    clientHandlers.add(tmp);

                    if(Guestturns == clientHandlers.size())
                    {
                        isHostTurn = true;
                        Guestturns = 0;
                    }
                }
           // } catch (IOException e) {
               // throw new RuntimeException(e);
            //}
            if(passCount()) // count turn passes in order to know if the game needs to be stopped
            {
                this.hostModel.stopGame();
            }
        }
        this.stopRemoteGame();
    }
    private void notifyRemotes(String[] wordDetails, String playerName)
    {
        //2|true|score|a,1.b2....|name|word(not full)|row|col|v\h
        String wordUpdate = "2|true|score|tiles|" + playerName + "|" + wordDetails[0] + "|" + wordDetails[1] + "|" + wordDetails[2] + "|" + wordDetails[3];

        for(GuestHandler ch : clientHandlers)
        {
            this.hostModel.write_to_socket(wordUpdate, ch.clientSocket);
        }
    }

    private void notifyBoardChanged(String wordinfo, Word word)
    {
        for(int i = 1; i < clientHandlers.size(); i++) // update other remotes a word was put into board
        {
            this.hostModel.write_to_socket(wordinfo, clientHandlers.get(i).clientSocket);
        }
        this.hostModel.updateMatrixBoard(word); // update host a word was put into board
    }

    private boolean passCount(){
        int passesCounter = 0;
        if(this.hostModel.current_player.wordDetails[0].equals("xxx") || this.hostModel.current_player.wordDetails[0].equals("XXX"))
        {
            passesCounter++;
        }
        for(int i = 0; i < this.clientHandlers.size(); i++)
        {
            if(this.clientHandlers.get(i).guestPlayer.wordDetails[0].equals("xxx") || this.clientHandlers.get(i).guestPlayer.wordDetails[0].equals("XXX"))
            {
                passesCounter++;
            }
        }

        if(passesCounter == this.hostModel.numbOfPlayers)
            return true;
        else
            return false;
    }

    public void stopRemoteGame(){
        try {
            Player winner = this.hostModel.current_player;
            for(GuestHandler guest : this.clientHandlers)
            {
                if(winner.getScore() < guest.guestPlayer.getScore())
                {
                    winner = guest.guestPlayer;
                }
            }
            String win = "The winner is: " + winner.name + " with: " + winner.getScore() + " points";
            this.hostModel.setMessage(win + ", Game Over");
            if(this.hostModel.gameServerSocket != null)
                if(this.hostModel.gameServerSocket.isClosed())
                    this.hostModel.gameServerSocket.close();
            win = "4|" + win;
            for(GuestHandler guest : this.clientHandlers)
            {
                this.hostModel.write_to_socket(win, guest.clientSocket);
                guest.clientSocket.close();
            }

            this.stop = true; // stop host server
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
