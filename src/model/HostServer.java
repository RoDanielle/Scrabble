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
    private boolean gameRunning;
    private boolean stop;
    private HostModel hostModel;

    /**
     * The HostServer function is the constructor of HostServer class.
     * It initializes all the basic parameters needed to host a remote game.
     *
     * @param host used to set the hostmodel field
     *
     */
    public HostServer(HostModel host){
        this.hostModel = host;
        this.clientHandlers = new ArrayList<>();
        this.isHostTurn = false; // Initially, it's the client's turn
        this.stop = true;
        this.gameRunning = host.gameRunning;
    }

    /**
     * The start function starts the server on port 8081.
     *
     */
    public void start(){
        this.stop = false;
        new Thread(()->startServer(8081)).start();
    }

    /**
     * The startServer function is responsible for creating a server socket and listening for client connections.
     * When a new client connects, the function creates a new ClientHandler object to handle the connection.
     * The function also starts the game when all clients have connected.
     *
     * @param port Specify the port that the server should listen on
     *
     * @throws IOException
     */
    private void startServer(int port){
        try {
            Scanner input = new Scanner(System.in);

            // Create a server socket to listen for client connections
            ServerSocket serverSocket = new ServerSocket(port);

            // Start accepting client connections
            while (!stop) {
                hostModel.setMessage("Waiting for remote players on ip 'localhost' and port " + port);
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();
                hostModel.setMessage("New remote player connected");

                // Create a new ClientHandler for the client and add it to the list
                GuestHandler clientHandler = new GuestHandler(clientSocket, this.hostModel);
                clientHandlers.add(clientHandler);

                // Start a new thread to handle the client
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

                // Start the game when the all client connects
                if (clientHandlers.size() == this.hostModel.getNumbOfPlayers() - 1) {
                    startGame();
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The startGame function is the main function of the server.
     * It runs a loop that alternates between host and client(remote players) turns, until the tile bag is empty or there are no more moves to be made.
     * When a new word was put on the board after each players turn the other players are notified.
     *
     * @return Void
     */
    private void startGame() {
        // Set the flag to indicate the client's turn
        isHostTurn = true;
        int Guestturns = 0;

        while (this.hostModel.gameRunning) {
            // Check if it's the servers (host) turn
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
                    if(hostScore < this.hostModel.getScore()) // host added a word into the board
                    {
                        notifyRemotes(this.hostModel.current_player.wordDetails, this.hostModel.current_player.name);
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

            if(passCount()) // count turn passes in order to know if the game needs to be stopped
            {
                this.hostModel.stopGame();
            }
        }
        this.stopRemoteGame();
    }
    /**
     * The notifyRemotes function is called when the host player has successfully placed a word on the board.
     * It sends an update to all remote players in the game, informing them of the new word details.
     *
     * @param wordDetails Pass the details of the word that was played to this function
     * @param playerName Identify the player who made the move
     *
     */
    private void notifyRemotes(String[] wordDetails, String playerName)
    {
        //2|true|score|a,1.b2....|name|word(not full)|row|col|v\h
        String wordUpdate = "2|true|score|tiles|" + playerName + "|" + wordDetails[0] + "|" + wordDetails[1] + "|" + wordDetails[2] + "|" + wordDetails[3];

        for(GuestHandler ch : clientHandlers)
        {
            this.hostModel.write_to_socket(wordUpdate, ch.clientSocket);
        }
    }

    /**
     * The notifyBoardChanged function is called when a word has been placed on the board by a remote player.
     * It sends a message to all other remote players that are connected to the server, notifying them of this change.
     * The message sent contains information about which player put down the word and what their score is now.
     * It also updated the host's board matrix that lets it know as well that a word was placed on the board.
     *
     * @param wordinfo Send the word information to other remotes
     * @param word Update the board in the host model
     *
     */
    private void notifyBoardChanged(String wordinfo, Word word)
    {
        for(int i = 1; i < clientHandlers.size(); i++) // update other remotes a word was put into board
        {
            this.hostModel.write_to_socket(wordinfo, clientHandlers.get(i).clientSocket);
        }
        this.hostModel.updateMatrixBoard(word); // update host a word was put into board
    }

    /**
     * The passCount function is used to determine if all players have pressed pass on their turn.
     *
     * @return True if all players have chosen to pass their on their turn in the current round, and false otherwise
     */
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
        if(passesCounter == this.hostModel.getNumbOfPlayers())
            return true;
        else
            return false;
    }

    /**
     * The stopRemoteGame function is called when the host decides to end the game.
     * It sends a message to all clients that the game has ended and who won.
     * The function then closes all sockets and files and stops running.
     *
     *
     */
    private void stopRemoteGame(){
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
                guest.closeAllFiles();
                guest.clientSocket.close();

            }
            this.stop = true; // stop host server
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
