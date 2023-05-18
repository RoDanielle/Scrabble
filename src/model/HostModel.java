package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
        this.board = new String[15][15];
        this.players = new ArrayList<>();
        this.tiles = new ArrayList<>();
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

    @Override
    public void updateBoard(String word, int row, int col, boolean vertical) {
        if (vertical) {
            for (int i = 0; i < word.length(); i++) {
                if (this.getBoard()[row + i][col] == null)
                    this.getBoard()[row + i][col] = word.indent(i);
            }
        } else // horizontal
        {
            for (int i = 0; i < word.length(); i++) {
                if (this.getBoard()[row][col + i] == null)
                    this.getBoard()[row][col + i] = word.indent(i);
            }
        }
    }

    @Override
    // Getter method to access the matrix
    public String[][] getBoard() {
        return this.board;
    }



    public void GameManagment(){
        //TODO - main:host\guest?

        Scanner input = new Scanner(System.in);

        System.out.println("do you want to play a local game or a remote game? for local enter: 1, for remote enter: 2");
        String gameType = input.nextLine();
        System.out.println("How many players are expected to participate in this game?");
        String num = input.nextLine();
        this.numbOfPlayers = Integer.parseInt(num);
        if(gameType == "1")
        {
            try {
                Socket server =new Socket("localhost",12345); //match the games server details
                startGame_local(server);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }


    public void startGame_remote(Socket server) {
        /*
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
            String player_request = null;
            String row = null;
            String col = null;
            String vertical = null;
            String fullWord;

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
            String[] args = {"Q","s1.txt","s2.txt",fullWord};

            //TODO handel response from server to the requested word (query at this point)


        }
    }


    class Player {
        String name;
        Socket socket;
        List<Tile> tiles;

        public Player() {
            this.name = null;
            this.socket = null;
            this.tiles = new ArrayList<>();
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
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

