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
case 4 :s - winner announcement
*/




package model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class GuestHandler implements Runnable {

    private HostModel myHost;
    public Socket clientSocket;
    private boolean isClientTurn;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;

    public Player guestPlayer;
    private boolean gameRunning;

    public Word addedWord;
    public String addedWordStr;

    /**
     * The GuestHandler function is the constructor.
     * It sets initializes all the information used for handling the communication between a guest and host.
     * The GuestHandler object handles all the guest requests and sends responses to these requests using other.
     *
     * @param clientSocket is the socket the host server created for a guset player
     * @param myHost Access the HostModel class
     *
     */
    public GuestHandler(Socket clientSocket, HostModel myHost) {
        this.myHost = myHost;
        this.clientSocket = clientSocket;
        this.isClientTurn = false;
        this.guestPlayer = new Player();
        this.gameRunning = true;

        this.addedWord = null;
        this.addedWordStr = "null";
    }

    // Getter for isClientTurn
    /**
     * The isClientTurn function returns a boolean value that indicates whether or not it is the guest player's turn.
     *
     * @return A boolean value
     */
    public boolean isClientTurn() {
        return isClientTurn;
    }

    // Setter for isClientTurn
    /**
     * The setClientTurn function sets the boolean value of isClientTurn to true or false.
     *
     *
     * @param isClientTurn Determine if it is the client's turn
     *
     */
    public void setClientTurn(boolean isClientTurn) {
        this.isClientTurn = isClientTurn;
    }

    /**
     * The closeAllFiles function closes all the files that were used to communicate with the guest player.
     *
     */
    public void closeAllFiles()
    {
        try {
            inFromClient.close();
            outToClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The giveTiles function is used to give the guest player their tiles.
     *
     *
     * @return The tiles that are given to the guest player as a string
     */
    private String giveTiles()
    {
        this.myHost.giveTiles(this.guestPlayer);
        String s_tiles = this.guestPlayer.strTiles.get(0);
        for(int i = 1; i < this.guestPlayer.strTiles.size(); i++)
        {
            s_tiles = s_tiles + "." + this.guestPlayer.strTiles.get(i);
        }
        return s_tiles; // return all players tiles as a String
    }

    /**
     * The turnNotifier function is called when it is the guest player's turn to play.
     * It sends a message to the client telling them that it is their turn.
     *
     */
    private void turnNotifier()
    {
        outToClient.println("1|your turn");
    }

    /**
     * The wordOkResponse function is called when the requested word was able to be placed on the board.
     * It removes the tiles used in that word from the player's tile rack, adds to their score, and sends
     * a response to the guest payer with the new score and new tiles.
     *
     * @param msgTag Tell the client what type of message it is
     * @param word Pass the word that was verified
     * @param row the row where the first letter of the word is placed
     * @param col the column where the first letter of the word is placed
     * @param vertical Determine if the word is vertical or horizontal
     * @param score the score earned for the word
     * @param w is the word object representing the word that was palced on the board
     *
     */
    public void wordOkResponse(String msgTag, String word, String row, String col,String vertical, int score, Word w){

        guestPlayer.removeStrTiles(word); // remove the tiles used in the word
        guestPlayer.addScore(score);
        String toGuest = msgTag + "|" + score + "|" + this.giveTiles() + "|" + guestPlayer.name + "|" + word + "|" + row + "|" + col + "|" + vertical;
        this.outToClient.println(toGuest);
        this.outToClient.flush();

        this.addedWord = w;
        this.addedWordStr = toGuest;
    }

    /**
     * The challengeFailed function is called when the guest player's requested challenge failed.
     * It sends a message to the guest player informing them that they have failed, and
     * decreases their score by 10 points.
     *
     *
     */
    public void challengeFailed()
    {
        String toGuest = "3" + "|false|" + guestPlayer.name;
        guestPlayer.decreaseScore(10);
        outToClient.println(toGuest);
        outToClient.flush();
    }

    /**
     * The wordPlacementFailed function is called when the word placement fails but the word was dictionary legal.
     * It sends a message to the guest player that their word placement request (query) failed, and then adds all of the tiles from
     * that word back into their tile rack.
     *
     * @param msgTag Identify the message type
     * @param w stores the tiles from the word that was attempted to be placed
     *
     */
    public void wordPlacementFailed(String msgTag, Word w)
    {
        String toGuest = msgTag + "|0|" + "null" + "|" + guestPlayer.name; //"2|true|0|null|name"
        outToClient.println(toGuest);
        outToClient.flush();

        for(int i = 0; i < w.getTiles().length; i++)
        {
            Tile t =  w.getTiles()[i];
            if (t != null)
                guestPlayer.tiles.add(t);
            w.getTiles()[i] = null;
        }
    }
    /**
     * The updateRequest function is used to update the wordDetails array of the guestPlayer object.
     * The function takes in a String array as an argument, and uses it to update the wordDetails
     * array of guestPlayer. If request[0] is "xxx" it means the user chose to pass its turn, then all elements of wordDetails are set
     * to null, otherwise they are updated with values from request[]. This function is called by handleRequest() when a new message containing a query request has been received from client.
     *
     * @param request Store the request from the client (guest player)
     *
     */
    private void updateRequest(String[] request)
    {
        this.guestPlayer.wordDetails[0] = request[1];
        if(!this.guestPlayer.wordDetails[0].equals("xxx") && !this.guestPlayer.wordDetails[0].equals("XXX"))
        {
            this.guestPlayer.wordDetails[1] = request[2];
            this.guestPlayer.wordDetails[2] = request[3];
            this.guestPlayer.wordDetails[3] = request[4];
        }
        else {
            this.guestPlayer.wordDetails[1] = "null";
            this.guestPlayer.wordDetails[2] = "null";
            this.guestPlayer.wordDetails[3] = "null";
        }
    }

    /**
     * The run function is the main function of this class.
     * This function is being called each time it is a guest players turn.
     * It handles all communication between the client(guest) and server(host),
     * handles the checks needed to be conducted for each request the guest asked for,as well as updating the board with new words,
     * maintaining a players object representing the guest on the host side and communicating with the game server in the name of the guest.
     *
     *
     */
    @Override
    public void run(){
        try {
            // Create input and output streams for the client socket
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToClient = new PrintWriter(clientSocket.getOutputStream(), true);

            String[] guestResponse = null;
            String toGuest = null;

            // sending seven tiles
            if(guestPlayer.tiles.size() == 0)
            {
                toGuest = "0|" + this.giveTiles();
                outToClient.println(toGuest);
            }

            if (isClientTurn()) {  // Check if it's the client's turn

                // Client's turn logic

                turnNotifier(); // case 1
                guestResponse = inFromClient.readLine().split("[|]");
                updateRequest(guestResponse);

                if (!guestResponse[1].equals("xxx") && !guestResponse[1].equals("XXX"))
                {
                    if (guestPlayer.name == null) // first round
                    {
                        guestPlayer.name = guestResponse[5];
                    }
                    System.out.println("REACHED PLAYER QUERY ");
                    this.myHost.ConnectToGameServer("localhost",8080);
                    if(this.myHost.testDictionary("Q",guestResponse[1],guestResponse[2],guestResponse[3],guestResponse[4],this.myHost.gameServerSocket)) // word found in dictionary
                    {
                        System.out.println("REACHED PLAYER QUERY TRUE");

                        toGuest = "2" + "|" + "true";
                        Word w = guestPlayer.create_word(guestResponse[1], guestResponse[2], guestResponse[3], guestResponse[4]);
                        System.out.println("guest handler: guest name = " + this.guestPlayer.name + " tiles str size: " + this.guestPlayer.strTiles.size() + this.guestPlayer.tiles.size());

                        int score = this.myHost.getBoardObject().tryPlaceWord(w);

                        if (score != 0) // word was put into board
                        {
                            wordOkResponse(toGuest, guestResponse[1], guestResponse[2], guestResponse[3],guestResponse[4],score, w);  // "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                        }
                        else {
                            wordPlacementFailed(toGuest, w); //"2|true|0|player name"
                        }
                    }
                    else
                    {
                        //Thread.sleep(3000);
                        toGuest = "2" + "|" + "false" + "|" + "null" + "|" + "null" + "|" + guestPlayer.name; //query return false "2|false|null|null|name"
                        this.outToClient.println(toGuest);
                        this.outToClient.flush();
                        System.out.println("guest handler - sent query failed response: " + toGuest);
                        guestResponse = inFromClient.readLine().split("[|]");
                        if (guestResponse[1].equals("c") || guestResponse[1].equals("C")) {
                            System.out.println("REACHED PLAYER CHALLENGE");

                            this.myHost.ConnectToGameServer("localhost", 8080);

                            if (this.myHost.testDictionary("C",guestResponse[2],guestResponse[3],guestResponse[4],guestResponse[5],this.myHost.gameServerSocket)) //challenge return true
                            {
                                toGuest = "3" + "|true";
                                Word w = guestPlayer.create_word(guestResponse[2], guestResponse[3], guestResponse[4], guestResponse[5]);
                                int score = this.myHost.getBoardObject().tryPlaceWord(w);
                                if (score != 0) // word was put into board
                                {
                                    score += 10;
                                    wordOkResponse(toGuest, guestResponse[2], guestResponse[3], guestResponse[4],guestResponse[5],score, w); // "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                } else {
                                    wordPlacementFailed(toGuest, w); // "3|true|0|player name"
                                }
                            }
                            else {
                                challengeFailed();
                            }
                        }
                    }
                }
                // Set the flag to indicate the server's turn is over
                isClientTurn = false;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
