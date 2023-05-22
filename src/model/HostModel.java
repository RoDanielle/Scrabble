/*
model:
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


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


import server.DictionaryManager;
import server.MyServer;
import server.ClientHandler;

import static java.lang.String.valueOf;

/*
import model.Board;
import model.Tile;
import model.Tile.Bag;
import model.Word;

 */


public class HostModel implements GameModel {


    String name;
    int score;
    String[][] board;
    List<Player> players;
    List<String> tiles;
    String latestWord;
    boolean gameRunning;
    Board boardObject;
    Tile.Bag bag;
    int numbOfPlayers;


    public HostModel() {
        this.name = null;
        this.score = 0;
        this.board = new String[15][15]; // needed only
        this.players = new ArrayList<>();
        this.tiles = new ArrayList<>(); // probably not needed since each player object has tiles array
        this.latestWord = null;
        this.gameRunning = true;
        this.boardObject = Board.getBoard();
        this.bag = Tile.Bag.getBag();
        numbOfPlayers = 0;

    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getLatestWord() { //?
        return this.latestWord;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public void addScore(int score) {
        this.score = score;
    }


    public String tileToString (Tile t)
    {
        String tileString  = null;
        tileString = valueOf(t.letter) + "," +Integer.toString(t.score);
        return tileString;
    }

    @Override
    // Getter method to access the matrix
    public String[][] getBoard() {
        return this.board;
    }

    public void stopGame(){
        this.gameRunning = false;
        //TODO- let all players know who the winner is
        int maxScore = 0;
        String winner = null;
        for (int i = 0; i<numbOfPlayers; i++) {
            if (players.get(i).score > maxScore) {
                maxScore = players.get(i).score;
                winner = null;
                winner = players.get(i).name;
            }
            if (players.get(i).score == maxScore)  //a case of more than one winner
                winner += "," + players.get(i).name;
        }
        System.out.println("The winners are:" + winner);

        //TODO - close all the sockets
        for (int i = 0; i < numbOfPlayers; i++) {  //need an indication whether the player is a guest or a host, we will close all the guests first
            try {
                players.get(i).socket.close();
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

    public void write_to_socket(String str, Socket _socket){
        try {
            PrintWriter outToServer = new PrintWriter(_socket.getOutputStream());
            outToServer.println(str);
            outToServer.flush();
            outToServer.close();
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

    public void GameManagement(){
        //TODO - main:host\guest? -- player was asked the player what it wants to be, in host case an HostModel was created and GameManagement function was called

        Scanner input = new Scanner(System.in);

        System.out.println("do you want to play a local game or a remote game? for local enter: 1, for remote enter: 2");
        String gameType = input.nextLine();
        System.out.println("How many players are expected to participate in this game? (including you)");
        String num = input.nextLine();
        this.numbOfPlayers = Integer.parseInt(num);
        if(gameType == "1")
        {
            try {
                Socket server =new Socket("localhost",12345); //match the games server (dictionary) details
                startGame_local(server);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            try {
                Socket server =new Socket("localhost",12345); //match the games server (dictionary) details
                int counter = 1;
                ServerSocket hostSocket = new ServerSocket(12345);
                //TODO - add host
                System.out.println("Host server started. Listening on port " + hostSocket.getLocalPort() + "and ip localhost");
                while (gameRunning) {
                    Socket clientSocket = hostSocket.accept();
                    counter += 1;
                    Player guest = new Player();
                    guest.setSocket(clientSocket);

                    players.add(guest);
                    if (counter == Integer.parseInt(num)) {
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
                            String s_tiles = null;
                            for(int i=0; i<7; i++)
                            {
                                Tile t = bag.getRand();
                                player.tiles.add(t);
                                s_tiles = s_tiles + "^" + this.tileToString(t);
                            }
                            s_tiles = "0|"+s_tiles;
                            PrintWriter outToGuest =new PrintWriter(player.socket.getOutputStream());
                            outToGuest.println(s_tiles);
                            outToGuest.flush();
                            outToGuest.close();
                        }
                        while(gameRunning)
                        {
                            if(players.get(0).socket == null)
                            {
                                //TODO - create new start method for host like start_local
                            }
                            startGame_remote(server ,players.get(0));
                            Player p = players.remove(0);
                            players.add(p);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void startGame_remote(Socket server, Player player) {

            String toGuest = null;
            String[] guestResponse = null;
            toGuest = "1|your turn";
            this.write_to_socket(toGuest, player.socket);

            guestResponse = this.read_from_socket(player.socket).split("|");

            if(guestResponse[1]!= "xxx")
            {
                if(player.name == null)
                {
                    player.name = guestResponse[5];
                }
                String word = fill_spaces(guestResponse[1], guestResponse[2],guestResponse[3],guestResponse[4]);
                String args = "Q,s1.txt,s2.txt," + word;
                this.write_to_socket(args, server); // sending query request to game server
                String server_response = this.read_from_socket(server); // response to query

                if(server_response == "true") // word found in dictionary
                {
                    toGuest = "2" + "|" + "true";
                    Word w = player.create_word(guestResponse[1], guestResponse[2],guestResponse[3],guestResponse[4]);
                    int score = this.boardObject.tryPlaceWord(w);
                    toGuest = toGuest + "|" + score;
                    if(score != 0) // word was put into board
                    {
                        player.addScore(score);
                        while(player.tiles.size() < 7)// fill missing tiles
                        {
                            Tile t = this.bag.getRand();
                            toGuest = toGuest + "|" + this.tileToString(t) + "^";
                            player.tiles.add(this.bag.getRand());
                        }
                        toGuest = toGuest + "|" + player.name + guestResponse[1] +"|" + guestResponse[2] +"|" + guestResponse[3] +"|" + guestResponse[4];
                        this.updateMatrixBoard(w); // updating matrix board
                        for(Player p : players)
                        {
                            if(p.socket != null) // updating all remote players a word was put into board
                            {
                                this.write_to_socket(toGuest, p.socket);  // "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                            }
                        }
                    }
                    else
                    {
                        toGuest = toGuest + player.name; //"2|true|0|name"
                        this.write_to_socket(toGuest, player.socket);
                        while(Arrays.stream(w.tiles).toList().size() > 0) // need to check if this works well -------------------
                        {
                            Tile t = Arrays.stream(w.tiles).toList().remove(0);
                            if(t != null)
                                player.tiles.add(t);   // give the player its tiles back
                        }
                    }
                }
                else
                {
                    toGuest = "2"+"|"+"false"+player.name; //query return false //"2|false|name"
                    guestResponse = this.read_from_socket(player.socket).split("|");
                    if(guestResponse[1] == "c")
                    {
                        args = "C,s1.txt,s2.txt," + word;
                        write_to_socket(args, server);
                        server_response = read_from_socket(server);
                        if(server_response == "true") //challenge return true
                        {
                            toGuest = "3" + "|true";
                            Word w = player.create_word(guestResponse[2], guestResponse[3],guestResponse[4],guestResponse[5]);
                            int score = this.boardObject.tryPlaceWord(w);
                            if(score != 0) // word was put into board
                            {
                                score+=10;
                                player.addScore(score); //bonus
                                toGuest = toGuest + "|" + score;
                                while(player.tiles.size() < 7)// fill missing tiles
                                {
                                    Tile t = this.bag.getRand();
                                    toGuest = toGuest + "|" + this.tileToString(t) + "^";
                                    player.tiles.add(this.bag.getRand());
                                }
                                toGuest = toGuest + "|" + player.name + guestResponse[1] +"|" + guestResponse[2] +"|" + guestResponse[3] +"|" + guestResponse[4];
                                this.updateMatrixBoard(w); // updating host matrix board
                                for(Player p : players)
                                {
                                    if(p.socket != null) // updating all remote players a word was put into board
                                    {
                                        this.write_to_socket(toGuest, p.socket);  // "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                    }
                                }
                            }
                            else
                            {
                                toGuest = toGuest + "|0|" + player.name; //"3|true|0|name" //challenge-V, try-X
                                this.write_to_socket(toGuest, player.socket);
                                while(Arrays.stream(w.tiles).toList().size() > 0) // need to check if this works well -------------------
                                {
                                    Tile t = Arrays.stream(w.tiles).toList().remove(0);
                                    if(t != null)
                                        player.tiles.add(t);   // give the player its tiles back
                                }
                            }
                        }
                        else
                        {
                            toGuest = "3" + "|false|" +player.name;
                            player.score -= 10;
                            write_to_socket(toGuest,player.socket);
                        }
                    }
                }
            }
    }


    public void startGame_local(Socket server) {
        for(int i = 2; i<this.numbOfPlayers+1; i++){
            System.out.println("please enter player" + i + "name:");
            Scanner input = new Scanner(System.in);
            String guestAns = input.nextLine();
            Player player = new Player();
            player.setName(guestAns);
            players.add(player);
        }

        System.out.println("start game");
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
            String player_request = null; // word
            String row = null;
            String col = null;
            String vertical = null;
            String fullWord;

            String current_player = this.players.get(0).name;
            System.out.println(current_player + " turn");
            System.out.println("please enter Word");
            Scanner input = new Scanner(System.in);
            String in = input.nextLine();
            player_request = in;
            System.out.println("Please enter row");
            in = input.nextLine();
            row = in;
            System.out.println("Please enter col");
            in = input.nextLine();
            col = in;
            System.out.println("Please enter v for vertical or h for horizontal");
            in = input.nextLine();
            vertical = in;
            fullWord = fill_spaces(player_request,row,col,vertical);
            String args = "Q,s1.txt,s2.txt," + fullWord;

            this.write_to_socket(args, server); // sending query request to game server
            String server_response = this.read_from_socket(server); // response to query

            if(server_response == "true") // word found in dictionary
            {
                Word w = players.get(0).create_word(player_request, row,col,vertical);
                int score = this.boardObject.tryPlaceWord(w);
                if(score != 0) // word was put into board
                {
                    System.out.println(current_player + " got " + score + " point for the word");
                    players.get(0).addScore(score);
                    while(players.get(0).tiles.size() < 7) // fill missing tiles
                        players.get(0).tiles.add(this.bag.getRand());

                    this.updateMatrixBoard(w); // updating matrix board
                }
                else
                {
                    while(Arrays.stream(w.tiles).toList().size() > 0) // need to check if this works well -------------------
                    {
                        Tile t = Arrays.stream(w.tiles).toList().remove(0);
                        if(t != null)
                            players.get(0).tiles.add(t);   // give the player its tiles back
                    }
                    System.out.println(current_player + " your word could not be put into the board, you get 0 points");
                }
            }
            else
            {
                System.out.println("word not found in dictionary, want to challenge? enter 'c' for challenge else enter 'pass'");
                in = input.nextLine();
                if(in == "c")
                {
                    args = "C,s1.txt,s2.txt," + fullWord;
                    this.write_to_socket(args, server); // sending challenge request to game server
                    server_response = this.read_from_socket(server); // response to challenge

                    if (server_response == "true")
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
                                players.get(0).tiles.add(this.bag.getRand());

                            this.updateMatrixBoard(w); // updating matrix board
                        }
                        else
                        {
                            System.out.println("challenge succeeded but word couldn't be put into board");
                        }
                    }
                    else
                    {
                        System.out.println("challenge failed you lose 10 points");
                        players.get(0).score -= 10;
                    }

                }
            }

            System.out.println("turn over");
            Player p = this.players.remove(0);
            this.players.add(p);
        }
    }



    class Player {
        String name;
        Socket socket;
        List<Tile> tiles;

        int score;

        public  Player() {
            this.name = null;
            this.socket = null;
            this.tiles = new ArrayList<>();
            this.score = 0;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public int getScore()
        {
            return this.score;
        }

        public void addScore(int score)
        {
            this.score += score;
        }

        public Word create_word(String input_word, String _row, String _col, String _vertical) {
            Tile[] wordarr = null;
            int row = Integer.parseInt(_row);
            int col = Integer.parseInt(_col);
            boolean vertical;
            if (_vertical == "v")
                vertical = true;
            else
                vertical = false;

            wordarr = new Tile[input_word.length()];
            int i = 0;
            for (char c : input_word.toCharArray()) {
                if (c == '_') {
                    wordarr[i] = null;
                } else {
                    for (int j = 0; j < this.tiles.size(); j++) {
                        if (c == this.tiles.get(j).letter) {
                            wordarr[i] = this.tiles.remove(j); // take the tiles
                            break;
                        }
                    }
                }
                i++;
            }

            Word word = new Word(wordarr, row, col, vertical);
            return word;
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

                        if (vertical == "v")
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






