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
            try {
                this.hostModel.ConnectToGameServer("localhost",8080); // connect to game server for each turn
                gameServer = this.hostModel.gameServerSocket;
                // Check if it's the server's turn
                if (isHostTurn) {
                    hostModel.setMessage("your turn, trying to connect to game server");
                    this.hostModel.playerTurn(gameServer, this.hostModel.current_player);
                    // Set the flag to indicate the client's turn
                    isHostTurn = false;
                }
                else {
                    // Client's turn logic
                    clientHandlers.get(0).setClientTurn(true);
                    clientHandlers.get(0).run();
                    clientHandlers.get(0).setClientTurn(false);
                    if(!clientHandlers.get(0).addedWordStr.equals(null)) // user put a word in the board
                    {
                        for(int i = 1; i < clientHandlers.size(); i++) // update other remotes a word was put into board
                        {
                            this.hostModel.write_to_socket(clientHandlers.get(0).addedWordStr, clientHandlers.get(i).clientSocket);
                        }
                        this.hostModel.updateMatrixBoard(clientHandlers.get(0).addedWord); // update host a word was put into board
                        clientHandlers.get(0).addedWord = null;
                        clientHandlers.get(0).addedWordStr = null;
                    }
                    GuestHandler tmp = clientHandlers.get(0);
                    clientHandlers.add(tmp);
                    Guestturns++;
                    if(Guestturns == clientHandlers.size() - 1)
                    {
                        isHostTurn = true;
                        Guestturns = 0;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            passCount(); // count turn passes in order to know if the game needs to be stopped
        }
        this.stopRemoteGame();
    }

    private void passCount(){
        int passesCounter = 0;
        if(this.hostModel.current_player.wordDetails[0].equals("xxx") || this.hostModel.current_player.wordDetails[0].equals("XXX"))
        {
            passesCounter++;
        }
        for(int i = 0; i < this.clientHandlers.size(); i++)
        {
            if((this.clientHandlers.get(i).p.wordDetails[0].equals("xxx") || this.clientHandlers.get(i).p.wordDetails[0].equals("XXX")))
            {
                passesCounter++;
            }
        }

        if(passesCounter == this.hostModel.numbOfPlayers)
        {
            this.hostModel.stopGame();
        }
    }

    public void stopRemoteGame(){
        try {
            Player winner = this.hostModel.current_player;
            for(GuestHandler guest : this.clientHandlers)
            {
                if(winner.getScore() < guest.p.getScore())
                {
                    winner = guest.p;
                }
            }
            String win = "The winner is:" + winner.name + "with: " + winner.getScore() + "points";
            this.hostModel.setMessage(win);
            this.hostModel.gameServerSocket.close();
            win = "4|" + win;
            for(GuestHandler guest : this.clientHandlers)
            {
                this.hostModel.write_to_socket(win, guest.clientSocket);
                guest.clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

