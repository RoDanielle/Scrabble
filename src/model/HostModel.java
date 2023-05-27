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

    public List<Player> getPlayers(){return this.players;}
    public int getNumbOfPlayers(){return this.numbOfPlayers;}


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

    public void write_to_socket(String str, Socket _socket){
        try {
            System.out.println(str);
            System.out.println(this.gameServerSocket.isClosed());
            PrintWriter outToServer = new PrintWriter(_socket.getOutputStream());
            System.out.println(this.gameServerSocket.isClosed());

            outToServer.println(str);
            outToServer.flush();
            System.out.println(this.gameServerSocket.isClosed());

//            outToServer.close();
            System.out.println(this.gameServerSocket.isClosed());

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

    public void ConnectToGameServer(String gameServerIP, int gameServerPort) throws IOException {
        Socket server; //TODO - match the games server (dictionary) details need to open a server somewhere
        server = new Socket(gameServerIP,gameServerPort);
        this.gameServerSocket = server;
    }

    public void GameManagement(String gameServerIP, int gameServerPort){

        //Loading word files to the server's dictionary??? There is a function that does this "loadFile" in "Dictionary"

        Scanner input = new Scanner(System.in);
        System.out.println("Do you want to play a local game or host a remote game? for local enter: 1, for remote enter: 2");
        String gameType = input.nextLine();

        if(gameType.equals("1"))
        {
            System.out.println("You are now hosting a local game");
            startGame_local(gameServerIP, gameServerPort);
            //this.gameServerSocket.close(); // needed to be called only after startGame_local stopped running, meaning after the game is over.
        }
        else { //remote
            try {
                System.out.println("How many players are expected to participate in this game? choose 1-4 (including you)");
                String num = input.nextLine();
                this.numbOfPlayers = Integer.parseInt(num);
                System.out.println("You are now hosting a remote game");
                System.out.println("please enter your name:");
                String ans = input.nextLine();
                Player hostPlayer = new Player();
                this.name = ans;
                hostPlayer.setName(ans);
                players.add(hostPlayer);

                //Socket server =new Socket(gameServerIP,gameServerPort); //TODO - match the games server (dictionary) details need to open a server somewhere
                ConnectToGameServer(gameServerIP,gameServerPort);
                int counter = 1;
                ServerSocket hostSocket = new ServerSocket(8081);
                System.out.println("Host server started. Listening on port: " + hostSocket.getLocalPort() + " and ip: localhost");

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
                            System.out.println("connecting to game server...");
                            try {
                                ConnectToGameServer(gameServerIP,gameServerPort);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("you are connected to game server on port " + this.gameServerSocket);

                            if(players.get(0).socket == null)
                            {
                                hostTurn(this.gameServerSocket);
                            }
                            else
                            {
                                startGame_remote(this.gameServerSocket ,players.get(0));
                            }
                            Player p = players.remove(0);
                            players.add(p);
                        }
                    }
                }
                stopRemoteGame();
                this.gameServerSocket.close();
                hostSocket.close();
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

            if(!guestResponse[1].equals("xxx") && !guestResponse[1].equals("XXX"))
            {
                if(player.name == null) //first round
                {
                    player.name = guestResponse[5];
                }
                String word = fill_spaces(guestResponse[1], guestResponse[2],guestResponse[3],guestResponse[4]);
                String args = "Q,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + word;
                this.write_to_socket(args, server); // sending query request to game server
                String server_response = this.read_from_socket(server); // response to query

                if(server_response.equals("true")) // word found in dictionary
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
                            if(this.bag.size() != 0)
                            {
                                Tile t = this.bag.getRand();
                                toGuest = toGuest + "|" + this.tileToString(t) + "^";
                                player.tiles.add(t);
                            }
                            else {
                                this.stopGame();
                                break;
                            }
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

                        List<Tile> tmplst = new ArrayList<>();
                        tmplst = Arrays.stream(w.tiles).toList();
                        while(tmplst.size() > 0)
                        {
                            Tile t = tmplst.remove(0);
                            if(t != null)
                                player.tiles.add(t);   // give the player its tiles back
                        }
                    }
                }
                else
                {
                    toGuest = "2"+"|"+"false"+player.name; //query return false //"2|false|name"
                    guestResponse = this.read_from_socket(player.socket).split("|");
                    if(guestResponse[1].equals("c") || guestResponse[1].equals("C"))
                    {
                        args = "C,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + word;
                        write_to_socket(args, server);
                        server_response = read_from_socket(server);
                        if(server_response.equals("true")) //challenge return true
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
                                    if(this.bag.size() != 0)
                                    {
                                        Tile t = this.bag.getRand();
                                        toGuest = toGuest + "|" + this.tileToString(t) + "^";
                                        player.tiles.add(t);
                                    }
                                    else {
                                        this.stopGame();
                                        break;
                                    }
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
                                List<Tile> tmplst = new ArrayList<>();
                                tmplst = Arrays.stream(w.tiles).toList();
                                while(tmplst.size() > 0)
                                {
                                    Tile t = tmplst.remove(0);
                                    if(t != null)
                                        player.tiles.add(t);   // give the player its tiles back
                                }
                            }
                        }
                        else
                        {
                            toGuest = "3" + "|false|" +player.name;
                            player.decreaseScore(10);
                            write_to_socket(toGuest,player.socket);
                        }
                    }
                }
            }
    }

    public void hostTurn(Socket server){
        String player_request = null; // word
        String row = null;
        String col = null;
        String vertical = null;
        String fullWord;

        String current_player = this.players.get(0).name;
        System.out.println(current_player + " turn");
        System.out.println("your tiles are: " + players.get(0).tiles);
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
        String args = "Q,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullWord;

        this.write_to_socket(args, server); // sending query request to game server
        String server_response = this.read_from_socket(server); // response to query

        if(server_response.equals("true")) // word found in dictionary
        {
            Word w = players.get(0).create_word(player_request, row,col,vertical);
            int score = this.boardObject.tryPlaceWord(w);
            if(score != 0) // word was put into board
            {
                System.out.println(current_player + " got " + score + " point for the word");
                players.get(0).addScore(score);
                while(players.get(0).tiles.size() < 7) // fill missing tiles
                {
                    if(this.bag.size() != 0)
                    {
                        players.get(0).tiles.add(this.bag.getRand());
                    }
                    else {
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
                this.write_to_socket(args, server); // sending challenge request to game server
                server_response = this.read_from_socket(server); // response to challenge

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
                            else {
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

        System.out.println("turn over");
        Player p = this.players.remove(0);
        this.players.add(p);
    }


    public boolean set_num_players(String num){
        if (num.equals("1") || num.equals("2") || num.equals("3") || num.equals("4")) {
            this.numbOfPlayers = Integer.parseInt(num);
            return true;
        }
        else
            return false;
    }

    public void create_players(ArrayList<String> names){

        for(int i = 0; i <=names.size(); i++){
            Player player = new Player();
            player.setName(names.get(i));
            players.add(player);
        }
    }

    public void order_players_turns(List<Player> players){ //Determines the order of players' turns
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
    }

    public void tile_completion(Player player){ //Gets a player and complements him to 7 tiles
        int size = player.tiles.size();
        if (player.tiles.size() < 7)
            for(int i=size+1; i<=7; i++)
            {
                Tile t = bag.getRand();
                player.tiles.add(t);
            }
    }


    public void startGame_local(String gameServerIP, int gameServerPort) {
        /*
        Boolean flag = false;
        Scanner input = new Scanner(System.in);
        String num = null;

        while (!flag) {
            System.out.println("How many players are expected to participate in this game? choose 1-4 (including you)");
            num = input.nextLine();
            if (num.equals("1") || num.equals("2") || num.equals("3") || num.equals("4"))
                flag = true;
            else {
                System.out.println("Error! try again");
                num = null;
            }
        }

        this.numbOfPlayers = Integer.parseInt(num);
        for(int i = 1; i <this.numbOfPlayers + 1; i++){
            if (i==1){
                System.out.println("Please enter your name:");
            }
            else {
                System.out.println("Please enter player " + i + " name:");
            }
            String ans = input.nextLine();
            Player player = new Player();
            player.setName(ans);
            players.add(player);
        }

         */
        Scanner input = new Scanner(System.in);
        while (this.numbOfPlayers == 0) {
            System.out.println("How many players are expected to participate in this game? choose 1-4 (including you)");
            if (set_num_players(input.nextLine()))
                System.out.println("Error! try again");
        }

        ArrayList<String> arrayNames = new ArrayList<>();
        for(int i = 1; i <=this.numbOfPlayers; i++) {
            if (i == 1)
                System.out.println("Please enter your name (host):");
            else
                System.out.println("Please enter player " + i + " name:");
            arrayNames.add(input.nextLine());
        }
        create_players(arrayNames);


        System.out.println("starting game");

        order_players_turns(this.players);

        for (int i = 0; i<this.numbOfPlayers; i++)
            tile_completion(this.players.get(i));


        while (this.gameRunning)
        {
            System.out.println("connecting to game server...");
            try {
                ConnectToGameServer(gameServerIP,gameServerPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("you are connected to game server on port " + this.gameServerSocket);

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
            /*

            System.out.println("please enter Word");
            player_request = input.nextLine();
            System.out.println("Please enter row");
            row = input.nextLine();
            System.out.println("Please enter col");
            col = input.nextLine();
            System.out.println("Please enter v for vertical or h for horizontal");
            vertical = input.nextLine();

             */


            player_request = get_word(this.players.get(0).tiles);


            fullWord = fill_spaces(player_request,row,col,vertical);
            String args = "Q,alice_in_wonderland.txt,Harry_Potter.txt,mobydick.txt,shakespeare.txt,The_Matrix.txt,pg10.txt," + fullWord;
            System.out.println(this.gameServerSocket.isClosed());
            this.write_to_socket(args, this.gameServerSocket); // sending query request to game server
            System.out.println(this.gameServerSocket.isClosed());

            //----------------------------------------------------------------------------------------------------------------------------------------------------
//            try {
//                //System.out.println("close? " + _socket.isClosed());
//                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(this.gameServerSocket.getInputStream()));
//                String serverResponse = inFromServer.readLine();
//                inFromServer.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            //------------------------------------------------------------------------------------------------------------------------------------------------------

            String server_response = this.read_from_socket(this.gameServerSocket); // response to query

            if(server_response.equals("true")) // word found in dictionary
            {
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

    public static class Player {
        String name;
        public Socket socket;
        List<Tile> tiles;

        int score;

        public  Player() {
            this.name = null;
            this.socket = null;
            this.tiles = new ArrayList<>();
            this.score = 0;
        }

        public List<Tile> getTiles(){
            return this.tiles;
        }
        public String getName(){
            return this.name;
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
        public void decreaseScore(int score)
        {
            this.score -= score;
        }

        public Word create_word(String input_word, String _row, String _col, String _vertical) {
            Tile[] word_arr = null;
            int row = Integer.parseInt(_row);
            int col = Integer.parseInt(_col);
            boolean vertical;
            if (_vertical.equals("V") || _vertical.equals("v"))
                vertical = true;
            else
                vertical = false;

            word_arr = new Tile[input_word.length()];
            int i = 0;
            for (char c : input_word.toCharArray()) {
                if (c == '_') {
                    word_arr[i] = null;
                } else {
                    for (int j = 0; j < this.tiles.size(); j++) {
                        if (c == this.tiles.get(j).letter) {
                            word_arr[i] = this.tiles.remove(j); // take the tiles
                            break;
                        }
                    }
                }
                i++;
            }

            Word word = new Word(word_arr, row, col, vertical);
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

    public String get_word(List<Tile> tiles) { //Getting a word from the player
        List<Tile> t = tiles;
        Scanner input = new Scanner(System.in);
        String player_request = null;
        String row = null;
        String col = null;
        String vertical = null;
        Boolean flag = false;


        while (!flag){
            System.out.println("Please enter a word: (built from your tiles)");
            player_request = input.nextLine();
            for (int i =0; i<player_request.length(); i++){
                for (int j =0 ; j<t.size(); j++){
                    if (t != null) {
                        if (player_request.charAt(i) == t.get(j).letter) {
                            t.remove(j);
                            break;
                        }
                        else if (player_request.charAt(i) == '_')
                            break;
                        else {
                            System.out.println("Error! Try again");
                            player_request = null;
                        }
                    }
                    if (t == null && player_request.charAt(i) != '_') {
                        System.out.println("Error! Try again");
                        player_request = null;
                    }
                }
            }
        }

        flag = false;
        while (!flag) {
            System.out.println("Please enter a row: 1-15");
            row = input.nextLine();
            if (Integer.parseInt(row)<=15 && Integer.parseInt(row)>=1)
                flag = true;
            else {
                System.out.println("Error! Try again");
                row = null;
            }
        }

        flag = false;
        while (!flag) {
            System.out.println("Please enter a col: 1-15");
            col = input.nextLine();
            if (Integer.parseInt(col)<=15 && Integer.parseInt(col)>=1)
                flag = true;
            else {
                System.out.println("Error! Try again");
                col = null;
            }
        }

        flag = false;
        while (!flag) {
            System.out.println("Please enter v for vertical or h for horizontal");
            vertical = input.nextLine();
            if (vertical.equals("V") || vertical.equals("v") || vertical.equals("H") || vertical.equals("h"))
                flag = true;
            else {
                System.out.println("Error! try again");
                vertical = null;
            }
        }
        return player_request;
    }

}






