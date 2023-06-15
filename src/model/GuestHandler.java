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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuestHandler implements Runnable {

    private HostModel myHost;
    public Socket clientSocket;
    private boolean isClientTurn;
    private BufferedReader inFromGameS;
    private PrintWriter outToGameS;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;

    public Player guestPlayer;
    private boolean gameRunning;

    public Word addedWord;
    public String addedWordStr;

    public GuestHandler(Socket clientSocket, HostModel myHost) {
        this.myHost = myHost;
        this.clientSocket = clientSocket;
        this.isClientTurn = false;
        this.guestPlayer = new Player();
        this.gameRunning = true;

        this.addedWord = null;  ///--------
        this.addedWordStr = "null";
    }

    // Getter for isClientTurn
    public boolean isClientTurn() {
        return isClientTurn;
    }

    // Setter for isClientTurn
    public void setClientTurn(boolean isClientTurn) {
        this.isClientTurn = isClientTurn;
    }

    public void closeAllFiles()  // TODO - check if we need to close the input output files when games finishes
    {
        try {
            inFromGameS.close();
            outToGameS.close();
            inFromClient.close();
            outToClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    private void turnNotifier()
    {
        outToClient.println("1|your turn");
        //this.myHost.write_to_socket("1|your turn",this.clientSocket);
    }

    public void wordOkResponse(String msgTag, String word, String row, String col,String vertical, int score, Word w){

        guestPlayer.removeStrTiles(word); // remove the tiles used in the word
        guestPlayer.addScore(score);
        String toGuest = msgTag + "|" + score + "|" + this.giveTiles() + "|" + guestPlayer.name + "|" + word + "|" + row + "|" +col + "|" + vertical;
        this.outToClient.println(toGuest);
        this.outToClient.flush();

        //this.myHost.write_to_socket(toGuest,this.clientSocket);
        this.addedWord = w;
        this.addedWordStr = toGuest;
    }

    public void challengeFailed()
    {
        String toGuest = "3" + "|false|" + guestPlayer.name;
        guestPlayer.decreaseScore(10);
        outToClient.println(toGuest);
        outToClient.flush();
        //this.myHost.write_to_socket(toGuest, this.clientSocket);
    }

    public void wordPlacementFailed(String msgTag, Word w)
    {
        String toGuest = msgTag + "|0|" + guestPlayer.name; //"2|true|0|name"
        outToClient.println(toGuest);
        outToClient.flush();
        //this.myHost.write_to_socket(toGuest,this.clientSocket);

        List<Tile> tmplst = new ArrayList<>();
        tmplst = Arrays.asList(w.tiles);
        while (tmplst.size() > 0) {
            Tile t = tmplst.remove(0);
            if (t != null)
                guestPlayer.tiles.add(t);   // give the player its tiles back
        }
    }
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
                // communication with game server for query and challenge
                //inFromGameS = new BufferedReader(new InputStreamReader(this.myHost.gameServerSocket.getInputStream()));
                //outToGameS = new PrintWriter(this.myHost.gameServerSocket.getOutputStream(), true);

                // Client's turn logic

                turnNotifier(); // case 1
                guestResponse = inFromClient.readLine().split("[|]");
                //guestResponse = this.myHost.read_from_socket(this.clientSocket).split("[|]"); // word from user
                updateRequest(guestResponse);

                if (!guestResponse[1].equals("xxx") && !guestResponse[1].equals("XXX"))
                {
                    if (guestPlayer.name == null) // first round
                    {
                        guestPlayer.name = guestResponse[5];
                    }
                    System.out.println("REACHED PLAYER QUERY ");
                    // TODO - CHANGE THE ! HERE , DELETE IT
                    this.myHost.ConnectToGameServer("localhost",8080);
                    if(this.myHost.testDictionary("Q",guestResponse[1],guestResponse[2],guestResponse[3],guestResponse[4],this.myHost.gameServerSocket)) // word found in dictionary
                    {
                        System.out.println("REACHED PLAYER QUERY TRUE");

                        toGuest = "2" + "|" + "true";
                        Word w = guestPlayer.create_word(guestResponse[1], guestResponse[2], guestResponse[3], guestResponse[4]);
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
                        toGuest = "2" + "|" + "false" + guestPlayer.name; //query return false "2|false|name"
                        guestResponse = inFromClient.readLine().split("[|]");
                        //guestResponse = this.myHost.read_from_socket(this.clientSocket).split("[|]");
                        if (guestResponse[1].equals("c") || guestResponse[1].equals("C")) {
                            System.out.println("REACHED PLAYER CHALLENGE");

                            this.myHost.ConnectToGameServer("localhost", 8080);

                            if (this.myHost.testDictionary("C",guestResponse[1],guestResponse[2],guestResponse[3],guestResponse[4],this.myHost.gameServerSocket)) //challenge return true
                            {
                                toGuest = "3" + "|true";
                                Word w = guestPlayer.create_word(guestResponse[2], guestResponse[3], guestResponse[4], guestResponse[5]);
                                int score = this.myHost.getBoardObject().tryPlaceWord(w);
                                if (score != 0) // word was put into board
                                {
                                    score += 10;
                                    wordOkResponse(toGuest, guestResponse[1], guestResponse[2], guestResponse[3],guestResponse[4],score, w); // "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                } else {
                                    wordPlacementFailed(toGuest, w); // "3|true|0|player name"
                                }
                            } else {
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
