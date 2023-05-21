package model;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


import server.DictionaryManager;
import server.MyServer;
import server.ClientHandler;

import static java.lang.String.valueOf;

import model.Board;
import model.Tile;
import model.Tile.Bag;
import model.Word;


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
    public void setScore(int score) {
        this.score = score;
    }


    public String tileToString (Tile t)
    {
        String tileString  = null;
        tileString = valueOf(t.letter) + "," +Integer.toString(t.score);
        return tileString;
    }

    public void updateMatrixBoard(Word w) {
        if (w.vertical)
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

    @Override
    // Getter method to access the matrix
    public String[][] getBoard() {
        return this.board;
    }

    public void stopGame(){
        this.gameRunning = false;
        //TODO - close all the sockets
    }



    public void GameManagement(){
        //TODO - main:host\guest? -- player was asked the player what it wants to be, in host case an HostModel was created and GameManagement function was called

        Scanner input = new Scanner(System.in);

        System.out.println("do you want to play a local game or a remote game? for local enter: 1, for remote enter: 2");
        String gameType = input.nextLine();
        System.out.println("How many players are expected to participate in this game?");
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

        try {
            PrintWriter outToGuest = new PrintWriter(player.socket.getOutputStream());
            BufferedReader inFromGuest = new BufferedReader(new InputStreamReader(player.socket.getInputStream()));
            String[] guestResponse = inFromGuest.readLine().split("|");

            outToGuest.println("your turn");
            outToGuest.flush();
            outToGuest.close();

            if(guestResponse[1]!= "xxx")
            {
                if(player.name == null)
                {
                    player.name = guestResponse[5];
                }
                String word = fill_spaces(guestResponse[1], guestResponse[2],guestResponse[3],guestResponse[4]);
                String args = "Q,s1.txt,s2.txt," + word;
                this.write_to_server(args, server); // sending query request to game server
                String server_response = this.read_from_server(server); // response to query

                String toGuest = null;
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
                            if(p.socket != null)
                            {
                                this.write_to_server(toGuest, p.socket);  // "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                            }
                        }
                    }
                    else
                    {

                        toGuest = toGuest + player.name; //"2|true|0|name"
                        this.write_to_server(toGuest, player.socket);
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
                    toGuest = "2"+"|"+"false"+player.name; //query return false // //"2|false|name"
                    guestResponse = inFromGuest.readLine().split("|");
                    if(guestResponse[1] == "c")
                    {
                        args = "C,s1.txt,s2.txt," + word;
                        write_to_server(args, server);
                        server_response = read_from_server(server);
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
                                this.updateMatrixBoard(w); // updating matrix board
                                for(Player p : players)
                                {
                                    if(p.socket != null)
                                    {
                                        this.write_to_server(toGuest, p.socket);  // "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                    }
                                }
                            }
                            else
                            {
                                toGuest = toGuest + "|0|" + player.name; //"3|true|0|name" //challenge-V, try-X
                                this.write_to_server(toGuest, player.socket);
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
                            write_to_server(toGuest,player.socket);
                        }
                    }

                }

            }


            //TODO - update all players boards


        } catch (IOException e) {
            throw new RuntimeException(e);
        }



/*
        System.out.println("please enter Word");
        Scanner input = new Scanner(System.in);
        request_to_server = "2";

        String in = input.nextLine();
        request_to_server = request_to_server + "|" + fill_spaces(in) + "|" + in;
        System.out.println("Please enter row");
        in = input.nextLine();
        request_to_server = request_to_server + "|" + in;
        System.out.println("Please enter col");
        in = input.nextLine();
        request_to_server = request_to_server + "|" + in;
        System.out.println("Please enter v for vertical or h for horizontal");
        in = input.nextLine();
        request_to_server = request_to_server + "|" + in;
        request_to_server = request_to_server + Q.get(0).user_name;

        write_to_server(request_to_server,socket);

        answer_from_server = read_from_server(socket); //TODO handel response from server to the requested word (query at this point)
        Socket = new Scanner(_inFromGuest);
        outToGuest = new PrintWriter(_outToGuest);
        inFromServer = new Scanner(_inFromServer);
        outToServer = new PrintWriter(_outToServer);

        String host_ans = null;
        String[] in_from_guest = in.next().split("|");
         */


    }


    public void startGame_local(Socket server) {
        for(int i = 1; i<this.numbOfPlayers+1; i++){
            System.out.println("please enter player" + i + "name");
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

            this.write_to_server(args, server); // sending query request to game server
            String server_response = this.read_from_server(server); // response to query

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
                    this.write_to_server(args, server); // sending challenge request to game server
                    server_response = this.read_from_server(server); // response to challenge

                    if (server_response == "true")
                    {
                        System.out.println("challenge succeeded");
                        //TODO - update the score bonus by challenge
                    }
                    else
                    {
                        System.out.println("challenge failed, your turn is over");
                    }

                }
            }

            Player p = this.players.remove(0);
            this.players.add(p);
        }
    }


    public void write_to_server(String str, Socket server_socket){
        try {
            PrintWriter outToServer = new PrintWriter(server_socket.getOutputStream());
            outToServer.println(str);
            outToServer.flush();
            outToServer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public String read_from_server(Socket server_socket) {
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));
            String serverResponse = inFromServer.readLine();
            inFromServer.close();
            return serverResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    class Player {
        String name;
        Socket socket;
        List<Tile> tiles;

        int score;

        public Player() {
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
            Tile[] wordarr;
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


/*
server input from model by cases - string structure
1. ask for seven random tiles: "1|user_name"
2. word input and all related parameters: "2|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)" (query only)
3. challenge request (if query returns false and user wants to): "3|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)" (challenge only)



server output to model by cases - - string structure
1. server sent seven tiles: "1|user_name|ch,score^ch,score^ch,score^ch,score^ch,score^ch,score^ch,score^
2. server sends response to word query: "2|"true"/"false"|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)"
3. server sends response to word query: "3|"true"/"false"|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)"

 */



/*
    public class BookScrabbleHandler implements ClientHandler{

        //DictionaryManager dm = DictionaryManager.get();
        PrintWriter out;
        Scanner in;

        List<Tile> all_players_tiles = new ArrayList<>();
        Tile.Bag bag=Tile.Bag.getBag();


        @Override
        public void handleClient(InputStream inFromclient, OutputStream outToClient) {
            out=new PrintWriter(outToClient);
            in=new Scanner(inFromclient);
            String server_ans = null;
            String[] in_from_user = in.next().split("|");



            switch (in_from_user[0]){
                case "1": // request seven tiles for round one  in: "1|username"
                    server_ans = "1" + "|" + in_from_user[1] + "|";
                    for(int i = 0; i < 7; i++)
                    {
                        String s_tile;
                        Tile tile = bag.getRand();
                        s_tile = valueOf(tile.letter) + "," + Integer.toString(tile.score) + "^";
                        server_ans = server_ans + s_tile;
                    }
                    out.println(server_ans);
                    out.flush();
                    break; // out:

                case "2": // enter word // in: "2|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)"
                    server_ans = "2";
                    DictionaryManager dm = new DictionaryManager();
                    Board board = new Board();
                    if(dm.query(in_from_user[1])) // need to creat the input for query before
                    {
                        server_ans = server_ans + "|" + "true";
                        Word word = create_word(in_from_user[2], in_from_user[3],in_from_user[4],in_from_user[5]);
                        int score = board.tryPlaceWord(word);

                        if(score != 0)
                        {
                            String s_score = null;
                            s_score = Integer.toString(score);
                            server_ans = server_ans + "|" + s_score;
                            server_ans = server_ans + "|" + in_from_user[2] + "|" + in_from_user[3]+ "|" +in_from_user[4] + "|" + in_from_user[5] + "|" + in_from_user[6] + "|";
                            for(int i = 0; i < in_from_user[2].length(); i++)
                            {
                                if(in_from_user[2].charAt(i) != '_')
                                {
                                    String s_tile;
                                    Tile tile = bag.getRand();
                                    s_tile = valueOf(tile.letter) + "," + Integer.toString(tile.score) + "^";
                                    server_ans = server_ans + s_tile;
                                }
                            }
                        }
                        else { // Q true but score = 0

                            // put tiles back
                            for(Tile t : word.tiles)
                            {
                                all_players_tiles.add(t);
                            }
                            server_ans = server_ans + "|" + "true" + "|" + "0" + in_from_user[2] + "|" + in_from_user[3]+ "|" +in_from_user[4] + "|" + in_from_user[5] + "|" + in_from_user[6];
                        }

                    }

                    else {
                        server_ans = server_ans + "|" + "false" + "|" + "0" + in_from_user[2] + "|" + in_from_user[3]+ "|" +in_from_user[4] + "|" + in_from_user[5] + "|" + in_from_user[6];
                    }
                    out.println(server_ans);
                    out.flush();
                    break;
                case "3":  // need to creat the input for challenge before





                    break;

            }








	    ////////////////////////////////////////////////this scope was in comment
		out=new PrintWriter(outToClient);
	    in=new Scanner(inFromclient);
	    String[] str = in.next().split(",");
	    String[] args = new String[str.length - 1];
	    System.arraycopy(str, 1, args, 0, str.length - 1);

	    if(str[0].equalsIgnoreCase("Q")){ // query
		    	DictionaryManager dm = new DictionaryManager();
		    	if(dm.query(args))
	                out.println("true");
	            else
	                out.println("false");
		      }
		      else // challenge
		        {
		            DictionaryManager dm = new DictionaryManager();
		            if(dm.challenge(args))
		                out.println("true");
		            else
		                out.println("false");
		        }
		        out.flush();
        }
        //////////////////////////////////////////////////////////////////////////

        public Word create_word(String input_word, String _row, String _col, String _vertical){
            Tile[] wordarr;
            int row = Integer.parseInt(_row);
            int col = Integer.parseInt(_col);
            boolean vertical;
            if (_vertical == "v")
                vertical = true;
            else
                vertical = false;

            wordarr = new Tile[input_word.length()];
            int i=0;
            for(char c: input_word.toCharArray()) {
                if(c == '_')
                {
                    wordarr[i] = null;
                }
                else {
                    for(int j = 0; j < all_players_tiles.size(); j++)
                    {
                        if(c == all_players_tiles.get(j).letter)
                        {
                            wordarr[i] = all_players_tiles.remove(j); // take the tiles
                            break;
                        }
                    }
                }
                i++;
            }

            Word word = new Word(wordarr, row,col,vertical);
            return word;
        }

        @Override
        public void close() {
            in.close();
            out.close();

        }


    }

 */

